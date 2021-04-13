package algorithms;

import objects.Solution;
import operators.Operator;
import operators.escapeOperators.EscapeOperator;
import operators.insertionOperators.GreedyInsertion;
import operators.insertionOperators.InsertionHeuristic;
import operators.insertionOperators.RegretKInsertion;
import operators.removalOperators.RandomRemoval;
import operators.removalOperators.RemovalHeuristic;
import operators.removalOperators.WorstRemoval;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;
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

    private final int UPDATE_SEGMENT = 250;

    @Override
    public Solution search() {
        return ALNS();
    }

    public Solution ALNS() {
        EscapeOperator escape = new EscapeOperator();
        // TODO: Create Map<Double, RemovalHeuristic> with score
        List<RemovalHeuristic> removal = Arrays.asList(new RandomRemoval(), new WorstRemoval()); // new RelatedRemoval
        List<InsertionHeuristic> insertion = Arrays.asList(new GreedyInsertion()); // new RegretKInsertion()

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currSolution = initialSolution.copy();
        double currCost = initialCost;

        int iterationsSinceLastImprovement = 0;
        for (int iteration = 0; iteration < ITERATIONS; iteration++) {

            if (iterationsSinceLastImprovement > 500) {
                currSolution = escape.operate(currSolution);
                iterationsSinceLastImprovement = 0;
            }

            Solution newSolution = currSolution.copy();

            int callsToRelocate = random.nextInt(4) + 1; // Remove 1 to 5 calls from currSolution

            List<Integer> removedCalls = removal.get(0).remove(newSolution, callsToRelocate);
            newSolution.removeCalls(removedCalls);
            newSolution = insertion.get(0).insert(newSolution, removedCalls);

            double newCost = newSolution.cost();

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
            if (iteration % UPDATE_SEGMENT == 0) {
                // updateOperators(operators);
            }
        }
        return bestSolution;
    }
}
