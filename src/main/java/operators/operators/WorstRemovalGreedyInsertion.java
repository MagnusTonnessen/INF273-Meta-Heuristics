package operators.operators;

import objects.Solution;

import java.util.List;

import static utils.Constants.greedyInsertion;
import static utils.Constants.worstRemoval;

public class WorstRemovalGreedyInsertion extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        List<Integer> removedCalls = worstRemoval.remove(solution, numberOfMoves);
        solution.removeCalls(removedCalls);
        return greedyInsertion.insert(solution, removedCalls);
    }
}
