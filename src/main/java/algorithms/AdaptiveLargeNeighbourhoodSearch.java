package algorithms;

import me.tongfei.progressbar.ProgressBar;
import objects.Solution;
import operators.escapeOperators.Escape;
import operators.operators.Operator;
import utils.VisualiseImprovement;
import utils.VisualiseOperatorWeights;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.E;
import static java.lang.Math.pow;
import static main.Main.initialCost;
import static main.Main.initialSolution;
import static main.Main.instanceName;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.VISUALIZE;
import static utils.Constants.random;
import static utils.Constants.randomRemovalGreedyInsertion;
import static utils.Constants.randomRemovalRegretKInsertion;
import static utils.Constants.relatedRemovalGreedyInsertion;
import static utils.Constants.relatedRemovalRegretKInsertion;
import static utils.Constants.worstRemovalGreedyInsertion;
import static utils.Constants.worstRemovalRegretKInsertion;

public class AdaptiveLargeNeighbourhoodSearch implements SearchingAlgorithm {

    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {
        return ALNS(iterations, runtime);
    }

    public Solution ALNS(int iterations, double runtime) {

        final double localSearchTime = 0.01;
        double startTime = System.currentTimeMillis();
        double endTime = System.currentTimeMillis() + runtime * 1000L * (1 - localSearchTime);

        final ProgressBar pb = new ProgressBar("Progress", (long) Math.ceil(runtime));
        pb.setExtraMessage(" ");

        final Set<Solution> foundSolutions = new HashSet<>();
        final int initTempIter = 200;
        final int escapeIter = 500;
        final int updateSegment = 100;

        Operator escape = new Escape();

        // Operators with weights
        List<OperatorWithWeights> operators = new ArrayList<>() {{
            add(new OperatorWithWeights(randomRemovalGreedyInsertion));
            add(new OperatorWithWeights(randomRemovalRegretKInsertion));
            add(new OperatorWithWeights(relatedRemovalGreedyInsertion));
            add(new OperatorWithWeights(relatedRemovalRegretKInsertion));
            add(new OperatorWithWeights(worstRemovalGreedyInsertion));
            add(new OperatorWithWeights(worstRemovalRegretKInsertion));
        }};

        // Normalize operators
        operators.forEach(op -> {
            op.setCurrentWeight(op.getCurrentWeight() / operators.size());
            op.setLastWeight(op.getLastWeight() / operators.size());
        });

        // Lists for probabilities and improvement visualisation
        Map<String, List<Double>> operatorProbabilities = new HashMap<>();
        operators.forEach(op -> {
            List<Double> list = new ArrayList<>();
            list.add(op.getCurrentWeight());
            operatorProbabilities.put(op.getOperator().getName(), list);
        });

        List<Double> improvement = new ArrayList<>();
        improvement.add(0.0);

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currSolution = initialSolution.copy();
        double currCost = initialCost;

        int iterSinceImp = 0;
        double T = 500;
        double alpha = 0.99;
        int iteration = 1;
        double deltas = 0;
        int numDeltas = 0;

        // 90 % of runtime is dedicated to ALNS
        while ((ITERATION_SEARCH && iteration < iterations * (1 - localSearchTime)) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            pb.stepTo((long) (System.currentTimeMillis() - startTime) / 1000);

            if (iteration % updateSegment == 0) {
                updateOperators(operators);
                operators.forEach(op -> operatorProbabilities.get(op.getOperator().getName()).add(op.getCurrentWeight()));
            }

            OperatorWithWeights operator = selectOperator(operators);
            Solution newSolution = operator.getOperator().operate(currSolution.copy(), random.nextInt(4) + 1);

            double newCost = newSolution.cost();
            double deltaE = newCost - currCost;
            boolean feasible = newSolution.isFeasible();
            boolean newSolutionFound = feasible && !foundSolutions.contains(newSolution);

            if (feasible) {
                foundSolutions.add(newSolution);
                if (deltaE < 0) {
                    iterSinceImp = 0;
                    currSolution = newSolution;
                    currCost = newCost;

                    if (currCost < bestCost) {
                        bestSolution = currSolution;
                        bestCost = currCost;
                    }
                } else if (iteration <= initTempIter && random.nextDouble() < 0.8) {
                    deltas += deltaE;
                    numDeltas++;
                    currSolution = newSolution;
                    currCost = newCost;
                } else if (iteration > initTempIter && random.nextDouble() < pow(E, -deltaE / T)) {
                    currSolution = newSolution;
                    currCost = newCost;
                    iterSinceImp++;
                }
            } else {
                iterSinceImp++;
            }

            if (iteration == initTempIter) {
                double timePerIter = (System.currentTimeMillis() - startTime) / initTempIter;
                double totalIter = (endTime - System.currentTimeMillis()) / timePerIter;
                T = findInitTemp(deltas / numDeltas);
                alpha = getAlpha(T / 5000, T, totalIter);
            }

            updateOperator(operator, newSolutionFound, feasible, newCost, currCost, bestCost);

            if (iterSinceImp > escapeIter) {
                currSolution = escape.operate(currSolution, random.nextInt(4) + 1);
                currCost = currSolution.cost();
                iterSinceImp = 0;
            }

            if (VISUALIZE) {
                improvement.add(100.0 * (initialCost - currCost) / initialCost);
            }
            T *= alpha;
            iteration++;
        }

        // 10 % of runtime is dedicated to local search
        currSolution = new LocalSearch().localSearch(bestSolution, bestCost, iterations * localSearchTime, runtime * localSearchTime, 0.33, 0.33);
        currCost = currSolution.cost();

        if (currCost < bestCost) {
            bestSolution = currSolution;
        }

        if (VISUALIZE) {
            improvement.add(100.0 * (initialCost - currCost) / initialCost);
            String name = instanceName;
            EventQueue.invokeLater(() -> new VisualiseOperatorWeights(name, operatorProbabilities));
            EventQueue.invokeLater(() -> new VisualiseImprovement(name, improvement));
        }

        return bestSolution;
    }

    private void updateOperators(List<OperatorWithWeights> operators) {
        operators.forEach(op -> {
            double newWeight = op.getLastWeight() * 0.8 + (op.getTimesUsed() == 0 ? 0 : 0.2 * op.getScore() / op.getTimesUsed());
            op.setLastWeight(op.getCurrentWeight());
            op.setCurrentWeight(newWeight);
        });

        double weightSum = operators.stream().mapToDouble(op -> op.currentWeight).sum();

        operators.forEach(op -> {
            op.setCurrentWeight(op.getCurrentWeight() / weightSum);
            op.resetTimesUsed();
            op.resetScore();
        });
    }

    private void updateOperator(OperatorWithWeights operator, boolean newSolutionFound, boolean feasible, double newCost, double currCost, double bestCost) {
        operator.incrementTimesUsed();
        operator.adjustScore(
                (feasible ? 0.5 : 0) +
                (feasible && newSolutionFound ? 1 : 0) +
                (feasible && newCost < currCost ? 2 : 0) +
                (feasible && newCost < bestCost ? 4 : 0)
        );
    }

    private double findInitTemp(double delta) {
        return -delta / Math.log(0.8);
    }

    private double getAlpha(double T_F, double T_0, double n) {
        return Math.pow(T_F / T_0, 1 / n);
    }

    private OperatorWithWeights selectOperator(List<OperatorWithWeights> operators) {

        double p = random.nextDouble();
        var cumulative = new Object() {
            double weight = 0;
        };

        return operators
                .stream()
                .sorted(Comparator.comparingDouble(OperatorWithWeights::getCurrentWeight))
                .dropWhile(op -> {
                    cumulative.weight += op.currentWeight;
                    return p >= cumulative.weight;
                })
                .findFirst()
                .orElse(operators.get(operators.size() - 1));
    }

    private static class OperatorWithWeights {
        private final Operator operator;
        private double lastWeight;
        private double currentWeight;
        private double score;
        private int timesUsed;

        public OperatorWithWeights(Operator operator) {
            this.operator = operator;
            this.currentWeight = 1;
            this.lastWeight = 1;
            this.score = 0;
            this.timesUsed = 1;
        }

        public Operator getOperator() {
            return operator;
        }

        public double getLastWeight() {
            return lastWeight;
        }

        public void setLastWeight(double lastWeight) {
            this.lastWeight = lastWeight;
        }

        public double getCurrentWeight() {
            return currentWeight;
        }

        public void setCurrentWeight(double currentWeight) {
            this.currentWeight = currentWeight;
        }

        public double getScore() {
            return score;
        }

        public void adjustScore(double score) {
            this.score += score;
        }

        public void resetScore() {
            this.score = 0;
        }

        public int getTimesUsed() {
            return timesUsed;
        }

        public void resetTimesUsed() {
            this.timesUsed = 0;
        }

        public void incrementTimesUsed() {
            this.timesUsed++;
        }

        @Override
        public String toString() {
            return "OperatorWithWeights{" +
                    "operator=" + operator.getClass().getSimpleName() +
                    ", lastWeight=" + lastWeight +
                    ", currentWeight=" + currentWeight +
                    ", score=" + score +
                    ", timesUsed=" + timesUsed +
                    '}';
        }
    }
}
