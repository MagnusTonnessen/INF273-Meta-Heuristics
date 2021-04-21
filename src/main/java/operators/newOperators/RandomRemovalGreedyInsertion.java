package operators.newOperators;

import objects.Solution;
import operators.oldOperators.Operator;

import java.util.List;

import static utils.Constants.greedyInsertion;
import static utils.Constants.random;
import static utils.Constants.randomRemoval;

public class RandomRemovalGreedyInsertion extends Operator {
    @Override
    public Solution operate(Solution solution) {
        List<Integer> removedCalls = randomRemoval.remove(solution, random.nextInt(4) + 1);
        solution.removeCalls(removedCalls);
        return greedyInsertion.insert(solution, removedCalls);
    }
}
