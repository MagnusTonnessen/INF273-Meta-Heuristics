package algorithms;

import objects.Solution;
import operators.escapeOperators.Escape;
import operators.newOperators.RandomRemovalGreedyInsertion;
import operators.newOperators.RandomRemovalRegretKInsertion;
import operators.newOperators.RelatedRemovalGreedyInsertion;
import operators.newOperators.RelatedRemovalRegretKInsertion;
import operators.newOperators.WorstRemovalGreedyInsertion;
import operators.newOperators.WorstRemovalRegretKInsertion;
import operators.oldOperators.Operator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.E;
import static java.lang.Math.pow;
import static main.Main.initialCost;
import static main.Main.initialSolution;
import static main.Main.iterations;
import static utils.Constants.ITERATIONS;
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
    public Solution search(double runtime) {
        return ALNS(runtime); // , 250, 0.97);
    }

    public Solution ALNS(double runtime) {

        final Set<Solution> foundSolutions = new HashSet<>();
        final int escapeIterations = 500;
        final int updateSegment = 250;

        Escape escape = new Escape();

        List<OperatorWithWeights> operators = new ArrayList<>() {{
            add(new OperatorWithWeights(new RandomRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new RandomRemovalRegretKInsertion()));
            add(new OperatorWithWeights(new RelatedRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new RelatedRemovalRegretKInsertion()));
            add(new OperatorWithWeights(new WorstRemovalGreedyInsertion()));
            add(new OperatorWithWeights(new WorstRemovalRegretKInsertion()));
        }};

        operators.forEach(op -> {
            op.setCurrentWeight(1.0 / operators.size());
            op.setLastWeight(1.0 / operators.size());
        });

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currSolution = initialSolution.copy();
        double currCost = initialCost;

        int iterationsSinceLastImprovement = 0;

        double T;
        double alpha;
        int iteration = 1;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        /*
        Find initial temperature and cooling factor
        */
        double deltas = 0;
        int numDeltas = 0;
        while (iteration <= 500) {

            OperatorWithWeights operator = selectOperator(operators);
            Solution newSolution = operator.getOperator().operate(currSolution.copy());

            double newCost = newSolution.cost();
            double delta = newCost - currCost;
            boolean feasible = newSolution.isFeasible();
            boolean newSolutionFound = feasible && !foundSolutions.contains(newSolution);

            if (feasible) {
                foundSolutions.add(newSolution);
                if (delta < 0) {
                    currSolution = newSolution;
                    currCost = newCost;

                    if (currCost < bestCost) {
                        bestSolution = currSolution;
                        bestCost = currCost;
                    }
                } else if (random.nextDouble() < 0.8) {
                    deltas += delta;
                    numDeltas++;
                    currSolution = newSolution;
                    currCost = newCost;
                }
            }

            updateOperator(operator, newSolutionFound, feasible, newCost, currCost, bestCost);

            iteration++;
        }

        T = findInitialTemperature(deltas / numDeltas, 0.8);
        alpha = getAlpha(1, T, 35000);

        System.out.println("\nInitial temperature: " + T);
        System.out.println("Alpha: " + alpha);
        if (T == 0.0) {
            return bestSolution;
        }
        while ((ITERATION_SEARCH && iteration < ITERATIONS) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            if (iteration % updateSegment == 0) {
                updateOperators(operators);
            }

            if (iterationsSinceLastImprovement > escapeIterations) {
                currSolution = escape.operate(currSolution);
                iterationsSinceLastImprovement = 0;
            }

            Solution newSolution = currSolution.copy();
            OperatorWithWeights operator = selectOperator(operators);
            newSolution = operator.getOperator().operate(newSolution);

            /*
            int callsToRelocate = random.nextInt(4) + 1; // Remove 1 to 4 calls from currSolution

            long startTime = System.currentTimeMillis();
            List<Integer> removedCalls = removal.get(random.nextInt(3) / 2).remove(newSolution, callsToRelocate);
            timeRemoving += System.currentTimeMillis() - startTime;

            newSolution.removeCalls(removedCalls);

            startTime = System.currentTimeMillis();
            newSolution = insertion.get(0).insert(newSolution, removedCalls);
            timeInserting += System.currentTimeMillis() - startTime;
            */

            double newCost = newSolution.cost();
            double deltaE = newCost - currCost;
            boolean feasible = newSolution.isFeasible();
            boolean newSolutionFound = feasible && !foundSolutions.contains(newSolution);

            if (feasible) {
                foundSolutions.add(newSolution);
                if (deltaE < 0) {
                    currSolution = newSolution;
                    currCost = newCost;

                    if (currCost < bestCost) {
                        bestSolution = currSolution;
                        bestCost = currCost;
                    }
                } else if (random.nextDouble() < pow(E, -deltaE / T)) {
                    currSolution = newSolution;
                    currCost = newCost;
                    iterationsSinceLastImprovement++;
                }
            } else {
                iterationsSinceLastImprovement++;
            }

            updateOperator(operator, newSolutionFound, feasible, newCost, currCost, bestCost);

            T *= alpha;
            iteration++;
        }

        iterations += iteration;
        System.out.println("Final temperature: " + T + "\n");
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
                (newSolutionFound ? 1 : 0) +
                        (newCost < currCost ? 2 : 0) +
                        (newCost < bestCost ? 4 : 0)
        );
        /*
        operator.adjustScore(
                (newSolutionFound ? 1 : -0.5) +
                        (feasible ? 0.5 : -0.5) +
                        (newCost < currCost ? 2 : -0.5) +
                        (newCost < bestCost ? 4 : 0));

         */
    }

    private double findInitialTemperature(double delta, double initialProbability) {
        /*
        p = e ^ ( -delta / T )
        ln ( p ) = -delta / T
        ln ( p ) / -delta = 1 / T
        T = 1 / ( ln ( p ) / -delta )
        */
        return 1.0 / (Math.log(initialProbability) / -delta);
    }

    public double getAlpha(double T_F, double T_0, int n) {
        return Math.pow(T_F / T_0, 1.0 / n);
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
