package operators.escapeOperators;

import objects.Solution;
import operators.oldOperators.Operator;

import java.util.List;

import static utils.Constants.greedyInsertion;
import static utils.Constants.random;
import static utils.Constants.randomRemoval;

public class NewEscape extends Operator {

    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        int i = 0;
        while (i < 20) {
            Solution newSolution = solution.copy();
            List<Integer> removedCalls = randomRemoval.remove(newSolution, random.nextInt(3) + 1);
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
