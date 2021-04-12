package algorithms;

import objects.Solution;
import operators.BruteForce;
import operators.OneInsert;
import operators.Operator;
import operators.ThreeExchange;
import operators.TransportAll;
import operators.TwoExchange;
import operators.removalOperators.WorstRemoval;
import operators.escapeOperators.EscapeOperator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static main.Main.solution;
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

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution solution = initialSolution.copy();
        double cost = initialCost;

        int iterationsSinceLastImprovement = 0;
        for (int i = 0; i < ITERATIONS; i++) {

            if (iterationsSinceLastImprovement > 500) {
                solution = escape.operate(solution);
                iterationsSinceLastImprovement = 0;
            }

            Solution newSolution = solution.copy();

            Operator removal = selectRemovalOperator(newSolution);
            Operator insertion = selectInsertionOperator(newSolution);
            int callsToRelocate = (int) Math.floor(Math.random() * 5 + 1); // Remove 1 to 5 calls from solution

            //List<Integer> removedCalls = removal.operate(newSolution);
            newSolution = insertion.operate(newSolution);

            double newCost = newSolution.cost();

            if (newCost < bestCost) {
                bestSolution = newSolution;
                bestCost = newCost;
            } else {
                iterationsSinceLastImprovement++;
            }
            // TODO: Greedy accept
            if (newCost < cost) {
                solution = newSolution;
                cost = newCost;
            }
            // updateOperators(operators);
        }
        return bestSolution;
    }

    private void rateOperator(Operator operator, int value) {

    }

    private Operator selectInsertionOperator(Solution newSolution) {
        return null;
    }

    private Operator selectRemovalOperator(Solution newSolution) {
        return null;
    }

    private void updateOperators(List<Operator> operators) {

    }

    private boolean accept(Solution newSolution, Solution solution) {
        return false;
    }

    private Operator selectOperator(List<Operator> operators) {
        double probability = random.nextDouble();
        return operators
                .stream()
                .sorted(Comparator.comparingDouble(Operator::getCumulativeProbability))
                .dropWhile(op -> op.getProbability() < probability)
                .findFirst()
                .orElse(operators.get(0));
    }
}
