package operators.operators;

import objects.Solution;
import objects.Vehicle;

import java.util.Collections;
import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class OneInsertFromDummy extends Operator {
    @Override
    public Solution operate(Solution solution) {

        Vehicle dummy = solution.getDummy();
        if (dummy.isEmpty()) {
            return solution;
        }
        int call = dummy.get(random.nextInt(dummy.size()));
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
