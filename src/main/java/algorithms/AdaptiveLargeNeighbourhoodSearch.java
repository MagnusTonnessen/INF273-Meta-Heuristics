package algorithms;

import objects.Solution;
import operators.BruteForce;
import operators.escapeOperators.EscapeOperator;
import operators.OneInsert;
import operators.Operator;
import operators.ThreeExchange;
import operators.TransportAll;
import operators.TwoExchange;
import operators.WorstRemoval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;
import static utils.Constants.random;

public class AdaptiveLargeNeighbourhoodSearch implements SearchingAlgorithm {
    @Override
    public Solution search() {
        return ALNS();
    }

    public Solution ALNS() {
        List<Operator> operators = new ArrayList<>() {{
            new OneInsert();
            new TwoExchange();
            new ThreeExchange();
            new BruteForce();
            new TransportAll();
            new WorstRemoval();
        }};
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
            int callsToRelocate = 1; // TODO
            List<Integer> removedCalls = removal.operate(newSolution);
            newSolution = insertion.operate(newSolution);

            double newCost = newSolution.cost();

            if (newCost < bestCost) {
                bestSolution = newSolution;
                bestCost = newCost;
            } else {
                iterationsSinceLastImprovement++;
            }
            if (accept(newSolution, solution)) {
                solution = newSolution;
            }
            updateOperators(operators);
        }
        return bestSolution;
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
