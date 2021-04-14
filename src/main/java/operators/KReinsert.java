package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class KReinsert extends Operator {
    @Override
    public Solution operate(Solution solution) {
        List<Integer> calls = randomRemoval.remove(solution, random.nextInt(4) + 1);
        return new Solution(solution) {{
            for (int call : calls) {
                List<Vehicle> validVehicles = problem.calls.get(call).getValidVehicles();
                moveCalls(call, validVehicles.isEmpty() ? getDummy() : validVehicles.get(random.nextInt(validVehicles.size())));
            }
        }};
    }
}
