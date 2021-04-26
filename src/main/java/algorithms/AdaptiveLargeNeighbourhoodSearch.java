package algorithms;

import objects.Solution;
import operators.escapeOperators.Escape;
import operators.escapeOperators.NewEscape;
import operators.newOperators.RandomRemovalGreedyInsertion;
import operators.newOperators.RandomRemovalRegretKInsertion;
import operators.newOperators.RelatedRemovalGreedyInsertion;
import operators.newOperators.RelatedRemovalRegretKInsertion;
import operators.newOperators.WorstRemovalGreedyInsertion;
import operators.newOperators.WorstRemovalRegretKInsertion;
import operators.oldOperators.Operator;
import utils.VisualiseImprovement;
import utils.VisualiseOperatorWeights;

import javax.swing.*;
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
import static java.util.stream.Collectors.toMap;
import static main.Main.initialCost;
import static main.Main.initialSolution;
import static main.Main.instanceName;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.random;

/*
ACCEPTANCE METHODS
Random Walk (RW)                |   Every new solution s' is accepted.
Greedy Acceptance (GRE)         |   The solution s' is only accepted, if it reduces the costs compared to the current solution s. This resembles Algorithm 1.
Simulated Annealing (SA)        |   Every improving solution s' is accepted. If c(s') > c(s), s' is accepted with probability exp(c(s)−c(s')/T) where T is the so-called temperature. The temperature decreases in every iteration by a factor φ.
Threshold Accepting (TA)        |   The solution s' is accepted, if c(s') − c(s) < T with a threshold T. The threshold is decreased in every iteration by a factor φ.
Old Bachelor Acceptance (OBA)   |   The solution s' is accepted, if c(s') − c(s) < T with a threshold T. The threshold is decreased after every acceptance a factor φ and increased after every rejection a factor ψ.
Great Deluge Algorithm (GDA)    |   The solution s' is accepted, if c(s') < L with a level L. The level will decrease by a factor φ only if the solution is accepted.
 */
public class AdaptiveLargeNeighbourhoodSearch implements SearchingAlgorithm {

    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {
        return ALNS(iterations, runtime); // , 250, 0.97);
    }

    public Solution ALNS(int iterations, double runtime) {

        final double localSearchTime = 0.2;
        double endTime = System.currentTimeMillis() + runtime * 1000L * (1 - localSearchTime);

        final Set<Solution> foundSolutions = new HashSet<>();
        final int initialTemperatureIterations = 200;
        final int escapeIterations = 500;
        final int updateSegment = 100;

        Operator escape = new Escape();

        // Operators with weights
        List<OperatorWithWeights> operators = new ArrayList<>() {{
            add(new OperatorWithWeights(new RandomRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new RandomRemovalRegretKInsertion()));
            add(new OperatorWithWeights(new RelatedRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new RelatedRemovalRegretKInsertion()));
            add(new OperatorWithWeights(new WorstRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new WorstRemovalRegretKInsertion()));
        }};

        // Normalize operators
        operators.forEach(op -> {
            op.setCurrentWeight(1.0 / operators.size());
            op.setLastWeight(1.0 / operators.size());
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

        int iterationsSinceLastImprovement = 0;
        double T = 500;
        double alpha = 0.99;
        int iteration = 1;
        double deltas = 0;
        int numDeltas = 0;

        // 90 % of runtime is dedicated to ALNS
        while ((ITERATION_SEARCH && iteration < iterations * (1 - localSearchTime)) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            if (iteration % updateSegment == 0) {
                updateOperators(operators);
                operators.forEach(op -> operatorProbabilities.get(op.getOperator().getName()).add(op.getCurrentWeight()));
            }

            OperatorWithWeights operator = selectOperator(operators);
            Solution newSolution = operator.getOperator().operate(currSolution.copy(), random.nextInt(3) + 1);

            double newCost = newSolution.cost();
            double deltaE = newCost - currCost;
            boolean feasible = newSolution.isFeasible();
            boolean newSolutionFound = feasible && !foundSolutions.contains(newSolution);

            if (feasible) {
                foundSolutions.add(newSolution);
                if (deltaE < 0) {
                    iterationsSinceLastImprovement = 0;
                    currSolution = newSolution;
                    currCost = newCost;

                    if (currCost < bestCost) {
                        bestSolution = currSolution;
                        bestCost = currCost;
                    }
                } else if (iteration <= initialTemperatureIterations && random.nextDouble() < 0.8) {
                    deltas += deltaE;
                    numDeltas++;
                    currSolution = newSolution;
                    currCost = newCost;
                } else if (iteration > initialTemperatureIterations && random.nextDouble() < pow(E, -deltaE / T)) {
                    currSolution = newSolution;
                    currCost = newCost;
                    iterationsSinceLastImprovement++;
                }
            } else {
                iterationsSinceLastImprovement++;
            }

            if (iteration == initialTemperatureIterations) {
                T = Math.min(5000, Math.max(5, findInitialTemperature(deltas / numDeltas)));
                alpha = getAlpha(T / 5000, T, iterations * (1 - localSearchTime));
                System.out.printf("\nInitial temperature: %.4f", T);
            }

            updateOperator(operator, newSolutionFound, feasible, newCost, currCost, bestCost);

            if (iterationsSinceLastImprovement > escapeIterations) {
                currSolution = escape.operate(currSolution, random.nextInt(4) + 1);
                currCost = currSolution.cost();
                iterationsSinceLastImprovement = 0;
            }

            improvement.add(100.0 * (initialCost - currCost) / initialCost);

            T *= alpha;
            iteration++;
        }

        // 10 % of runtime is dedicated to local search
        currSolution = new LocalSearch().localSearch(bestSolution, bestCost, iterations * localSearchTime, runtime * localSearchTime, 0.33, 0.33);
        currCost = currSolution.cost();

        if (currCost < bestCost) {
            bestSolution = currSolution;
        }

        improvement.add(100.0 * (initialCost - currCost) / initialCost);

        System.out.printf("\nFinal temperature: %.4f\n", T);
        String name = instanceName;
        // EventQueue.invokeLater(() -> new VisualiseOperatorWeights(name, operatorProbabilities));
        EventQueue.invokeLater(() -> new VisualiseImprovement(name, improvement));
        return bestSolution;
    }

    private void updateOperators(List<OperatorWithWeights> operators) {
        operators.forEach(op -> {
            double newWeight = op.getLastWeight() * 0.8 + 0.2 * op.getScore() / op.getTimesUsed();
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

    private double findInitialTemperature(double delta) {
        /*
        p = e ^ ( -delta / T )
        ln ( p ) = -delta / T
        ln ( p ) / -delta = 1 / T
        T = 1 / ( ln ( p ) / -delta )
        */
        return 1.0 / (Math.log(0.8) / -delta);
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
                .sorted(Comparator.comparingDouble(op -> op.currentWeight))
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
            this.timesUsed = 1;
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
