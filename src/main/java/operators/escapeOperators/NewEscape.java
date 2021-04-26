package operators.escapeOperators;

import objects.Solution;
import operators.insertionHeuristics.GreedyInsertion;
import operators.oldOperators.Operator;
import operators.removalHeuristics.RandomRemoval;
import operators.removalHeuristics.RemovalHeuristic;

import java.util.List;

import static utils.Constants.random;

public class NewEscape extends Operator {

    RemovalHeuristic randomRemoval = new RandomRemoval();
    GreedyInsertion greedyInsertion = new GreedyInsertion();

    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        int i = 0;
        while (i < 20) {
            List<Integer> removedCalls = randomRemoval.remove(solution, random.nextInt(3) + 1);
            Solution newSolution = solution.copy();
            newSolution.removeCalls(removedCalls);
            newSolution = greedyInsertion.insert(newSolution, removedCalls);
            if (newSolution.isFeasible()) {
                solution = newSolution;
                i++;
            }
        }
        return solution;
    }
}
