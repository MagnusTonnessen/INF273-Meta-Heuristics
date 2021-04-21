package operators.oldOperators;

import objects.Solution;

import java.util.List;

import static utils.Constants.greedyInsertion;
import static utils.Constants.random;
import static utils.Constants.randomRemoval;
import static utils.Constants.worstRemoval;

public class KReinsert extends Operator {
    @Override
    public Solution operate(Solution solution) {
        Solution newSolution = solution.copy();
        List<Integer> calls = (random.nextDouble() < 0.0 ? randomRemoval : worstRemoval).remove(solution, random.nextInt(4) + 1);
        newSolution.removeCalls(calls);
        return greedyInsertion.insert(newSolution, calls);
        /*
        return new Solution(solution) {{
            for (int call : calls) {
                List<Vehicle> validVehicles = problem.calls.get(call).getValidVehicles();
                moveCalls(call, validVehicles.isEmpty() ? getDummy() : validVehicles.get(random.nextInt(validVehicles.size())));
            }
        }};
        */
    }
}
