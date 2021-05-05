package operators.operators;

import objects.Solution;

import java.util.List;

import static utils.Constants.randomRemoval;
import static utils.Constants.regretKInsertion;

public class RandomRemovalRegretKInsertion extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        List<Integer> removedCalls = randomRemoval.remove(solution, numberOfMoves);
        solution.removeCalls(removedCalls);
        return regretKInsertion.insert(solution, removedCalls);
    }
}
