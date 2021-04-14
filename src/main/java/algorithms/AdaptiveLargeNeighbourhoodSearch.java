package algorithms;

import objects.Solution;
import operators.BruteForce;
import operators.KReinsert;
import operators.OneInsert;
import operators.OneInsertFromDummy;
import operators.Operator;
import operators.Random;
import operators.ThreeExchange;
import operators.TwoExchange;
import operators.escapeOperators.Escape;
import operators.insertionHeuristics.GreedyInsertion;
import operators.insertionHeuristics.InsertionHeuristic;
import operators.removalHeuristics.RandomRemoval;
import operators.removalHeuristics.RemovalHeuristic;
import operators.removalHeuristics.WorstRemoval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static main.Main.initialCost;
import static main.Main.initialSolution;
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

    private final int UPDATE_SEGMENT = 100;

    @Override
    public Solution search(double runtime) {
        return ALNS(runtime);
    }

    public Solution ALNS(double runtime) {

        long timeRemoving = 0;
        long timeInserting = 0;

        Escape escape = new Escape();

        List<OperatorWithWeights> operators = new ArrayList<>() {{
            add(new OperatorWithWeights(new OneInsert()));
            add(new OperatorWithWeights(new OneInsertFromDummy()));
            add(new OperatorWithWeights(new KReinsert()));
            add(new OperatorWithWeights(new TwoExchange()));
            add(new OperatorWithWeights(new ThreeExchange()));
            add(new OperatorWithWeights(new BruteForce()));
            add(new OperatorWithWeights(new Random()));
        }};

        operators.forEach(op -> op.setCurrentWeight(op.getCurrentWeight() / operators.size()));

        List<RemovalHeuristic> removal = Arrays.asList(new WorstRemoval(), new RandomRemoval()); // new RelatedRemoval
        List<InsertionHeuristic> insertion = Arrays.asList(new GreedyInsertion()); // new RegretKInsertion()

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currSolution = initialSolution.copy();
        double currCost = initialCost;

        int iterationsSinceLastImprovement = 0;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < ITERATIONS) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            if (iterationsSinceLastImprovement > 500) {
                currSolution = escape.operate(currSolution);
                iterationsSinceLastImprovement = 0;
            }

            Solution newSolution = currSolution.copy();
            Operator operator = selectOperator(operators);
            newSolution = operator.operate(newSolution);

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

            if (newSolution.isFeasible()) {
                if (newCost < bestCost) {
                    bestSolution = newSolution;
                    bestCost = newCost;
                } else {
                    iterationsSinceLastImprovement++;
                }
                // TODO: Greedy accept
                if (newCost < currCost) {
                    currSolution = newSolution;
                    currCost = newCost;
                }
            }
            if (iteration % UPDATE_SEGMENT == 0) {
                // updateOperators(operators);
            }
            iteration++;
        }
        // System.out.println("\nTime removing: " + timeRemoving/1000.0);
        // System.out.println("Time inserting: " + timeInserting/1000.0);
        // System.out.println("\nIterations done: " + iteration);
        return bestSolution;
    }

    private Operator selectOperator(List<OperatorWithWeights> operators) {

        double p = random.nextDouble();
        var cumulative = new Object() { double weight = 0; };

        return operators
                .stream()
                .sorted(Comparator.comparingDouble(op -> op.currentWeight))
                .dropWhile(op -> {
                    cumulative.weight += op.currentWeight;
                    return p < cumulative.weight; })
                .findFirst()
                .orElseThrow()
                .getOperator();
    }

    private static class OperatorWithWeights {
        private final Operator operator;
        private double lastWeight;
        private double currentWeight;

        public OperatorWithWeights(Operator operator) {
            this.operator = operator;
            this.currentWeight = 1;
            this.lastWeight = 1;
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
    }
}
