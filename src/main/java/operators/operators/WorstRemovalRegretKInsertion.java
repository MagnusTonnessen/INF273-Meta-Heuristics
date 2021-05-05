package operators.operators;

import objects.Solution;

import java.util.List;

import static utils.Constants.regretKInsertion;
import static utils.Constants.worstRemoval;

public class WorstRemovalRegretKInsertion extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        List<Integer> removedCalls = worstRemoval.remove(solution, numberOfMoves);
        solution.removeCalls(removedCalls);
        return regretKInsertion.insert(solution, removedCalls);
    }
}
