package operators.newOperators;

import objects.Solution;
import operators.oldOperators.Operator;

import java.util.List;

import static utils.Constants.random;
import static utils.Constants.regretKInsertion;
import static utils.Constants.worstRemoval;

public class WorstRemovalRegretKInsertion extends Operator {
    @Override
    public Solution operate(Solution solution) {
        List<Integer> removedCalls = worstRemoval.remove(solution, random.nextInt(4) + 1);
        solution.removeCalls(removedCalls);
        return regretKInsertion.insert(solution, removedCalls);
    }
}
