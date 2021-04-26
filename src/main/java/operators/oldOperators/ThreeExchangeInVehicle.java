package operators.oldOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static utils.Constants.random;

public class ThreeExchangeInVehicle extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {

        Vehicle vehicle = solution.get(random.nextInt(solution.size() - 1));

        if (vehicle.size() < 4) {
            return solution;
        }

        int firstCall = vehicle.get(random.nextInt(vehicle.size()));
        int secondCall = vehicle.get(random.nextInt(vehicle.size()));
        int thirdCall = vehicle.get(random.nextInt(vehicle.size()));

        if (firstCall == secondCall || secondCall == thirdCall || firstCall == thirdCall) {
            return solution.copy();
        }

        return new Solution(solution) {{
            Vehicle vehicle = getVehicleFromCall(firstCall);

            List<Integer> indexes1 = vehicle.indexes(firstCall);
            List<Integer> indexes2 = vehicle.indexes(secondCall);
            List<Integer> indexes3 = vehicle.indexes(thirdCall);

            vehicle.set(indexes1.get(0), thirdCall);
            vehicle.set(indexes1.get(1), thirdCall);
            vehicle.set(indexes2.get(0), firstCall);
            vehicle.set(indexes2.get(1), firstCall);
            vehicle.set(indexes3.get(0), secondCall);
            vehicle.set(indexes3.get(1), secondCall);
        }};
    }
}
