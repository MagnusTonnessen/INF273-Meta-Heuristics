package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class TwoExchange implements Operator {
    @Override
    public Solution operate(Solution solution) {

        int firstCall = random.nextInt(problem.nCalls);
        int secondCall = random.nextInt(problem.nCalls);

        if (firstCall == secondCall) {
            return solution.copy();
        }

        return new Solution(solution) {{
            Vehicle vehicle1 = getVehicleFromCall(firstCall);
            Vehicle vehicle2 = getVehicleFromCall(secondCall);

            List<Integer> indexes1 = vehicle1.indexes(firstCall);
            List<Integer> indexes2 = vehicle2.indexes(secondCall);

            vehicle1.set(indexes1.get(0), secondCall);
            vehicle1.set(indexes1.get(1), secondCall);
            vehicle2.set(indexes2.get(0), firstCall);
            vehicle2.set(indexes2.get(1), firstCall);
        }};
    }
}
