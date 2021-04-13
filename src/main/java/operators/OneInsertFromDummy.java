package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class OneInsertFromDummy extends Operator {
    @Override
    public Solution operate(Solution solution) {

        Vehicle dummy = solution.getDummy();
        int call = dummy.get(random.nextInt(dummy.size()));
        List<Vehicle> validVehicles = problem.calls.get(call).getValidVehicles();

        if (validVehicles.isEmpty()) {
            return solution.copy();
        }

        return new Solution(solution) {{
            moveCalls(call, validVehicles.get(random.nextInt(validVehicles.size())));
        }};
    }
}
