package operators.operators;

import objects.Solution;

import java.util.List;

import static utils.Constants.greedyInsertion;
import static utils.Constants.relatedRemoval;

public class RelatedRemovalGreedyInsertion extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        List<Integer> removedCalls = relatedRemoval.remove(solution, numberOfMoves);
        solution.removeCalls(removedCalls);
        return greedyInsertion.insert(solution, removedCalls);
    }
}
