package operators.newOperators;

import objects.Solution;
import operators.oldOperators.Operator;

import java.util.List;

import static utils.Constants.random;
import static utils.Constants.regretKInsertion;
import static utils.Constants.relatedRemoval;

public class RelatedRemovalRegretKInsertion extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        List<Integer> removedCalls = relatedRemoval.remove(solution, numberOfMoves);
        solution.removeCalls(removedCalls);
        return regretKInsertion.insert(solution, removedCalls);
    }
}
