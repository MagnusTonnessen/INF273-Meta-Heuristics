package operators.oldOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.Collections;
import java.util.List;

import static main.Main.problem;
import static utils.Constants.greedyInsertion;
import static utils.Constants.random;
import static utils.Constants.randomRemoval;
import static utils.Constants.worstRemoval;

public class OneInsert extends Operator {

    @Override
    public Solution operate(Solution solution) {

        List<Integer> calls = randomRemoval.remove(solution, 1);
        int call = calls.get(random.nextInt(calls.size())); // random.nextInt(problem.nCalls);
        List<Vehicle> validVehicles = problem.calls.get(call).getValidVehicles();

        if (validVehicles.isEmpty()) {
            return solution.copy();
        }

        Solution newSolution = solution.copy();
        newSolution.moveCalls(call, validVehicles.get(random.nextInt(validVehicles.size())));
        return newSolution;
    }
}
