package operators;

import objects.Solution;

import static main.Main.problem;
import static utils.Constants.random;

public class OneInsert implements Operator {

    @Override
    public Solution operate(Solution solution) {

        int call = random.nextInt(problem.nCalls);
        int[] validVehicles = problem.calls.get(call).validVehicles;

        if (validVehicles.length < 1) {
            return solution.copy();
        }

        return new Solution(solution) {{
            moveCalls(call, validVehicles[random.nextInt(validVehicles.length)]);
        }};
    }
}
