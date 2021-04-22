package operators.oldOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class TwoExchangeInVehicle extends Operator {
    @Override
    public Solution operate(Solution solution) {

        Vehicle vehicle = solution.get(random.nextInt(solution.size() - 1));

        if (vehicle.size() < 4) {
            return solution;
        }

        int firstCall = vehicle.get(random.nextInt(vehicle.size()));
        int secondCall = vehicle.get(random.nextInt(vehicle.size()));

        if (firstCall == secondCall) {
            return solution.copy();
        }

        return new Solution(solution) {{
            Vehicle vehicle = getVehicleFromCall(firstCall);

            List<Integer> indexes1 = vehicle.indexes(firstCall);
            List<Integer> indexes2 = vehicle.indexes(secondCall);

            vehicle.set(indexes1.get(0), secondCall);
            vehicle.set(indexes1.get(1), secondCall);
            vehicle.set(indexes2.get(0), firstCall);
            vehicle.set(indexes2.get(1), firstCall);
        }};
    }
}
