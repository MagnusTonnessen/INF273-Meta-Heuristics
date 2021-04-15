package operators.operators;

import objects.Solution;
import objects.Vehicle;

import java.util.Collections;
import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class OneInsert extends Operator {

    @Override
    public Solution operate(Solution solution) {

        List<Integer> calls = randomRemoval.remove(solution, 1);
        int call = calls.get(random.nextInt(calls.size())); // random.nextInt(problem.nCalls);
        List<Vehicle> validVehicles = problem.calls.get(call).getValidVehicles();

        if (validVehicles.isEmpty()) {
            return solution.copy();
        }

        solution.removeCall(call);
        return greedyInsertion.insert(solution, Collections.singletonList(call));
        /*
        return new Solution(solution) {{
            moveCalls(call, validVehicles.get(random.nextInt(validVehicles.size())));
        }};
        */
    }
}
