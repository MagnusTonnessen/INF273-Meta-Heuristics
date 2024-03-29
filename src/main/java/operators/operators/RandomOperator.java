package operators.operators;

import objects.Solution;

import java.util.Collections;

import static main.Main.problem;
import static utils.Constants.random;

public class RandomOperator extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {

        return new Solution(solution) {{
            for (int call = 0; call < problem.nCalls; call++) {
                moveCallRandom(call, random.nextInt(problem.nVehicles + 1));
            }

            for (int vehicle = 0; vehicle < problem.nVehicles; vehicle++) {
                Collections.shuffle(get(vehicle));
            }
        }};
    }
}
