package operators.operators;

import objects.Solution;
import objects.Vehicle;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class ThreeExchange extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {

        int firstCall = random.nextInt(problem.nCalls);
        int secondCall = random.nextInt(problem.nCalls);
        int thirdCall = random.nextInt(problem.nCalls);

        if (firstCall == secondCall || secondCall == thirdCall || firstCall == thirdCall) {
            return solution.copy();
        }

        return new Solution(solution) {{
            Vehicle vehicle1 = getVehicleFromCall(firstCall);
            Vehicle vehicle2 = getVehicleFromCall(secondCall);
            Vehicle vehicle3 = getVehicleFromCall(thirdCall);

            List<Integer> indexes1 = vehicle1.indexes(firstCall);
            List<Integer> indexes2 = vehicle2.indexes(secondCall);
            List<Integer> indexes3 = vehicle3.indexes(thirdCall);

            vehicle1.set(indexes1.get(0), thirdCall);
            vehicle1.set(indexes1.get(1), thirdCall);
            vehicle2.set(indexes2.get(0), firstCall);
            vehicle2.set(indexes2.get(1), firstCall);
            vehicle3.set(indexes3.get(0), secondCall);
            vehicle3.set(indexes3.get(1), secondCall);
        }};
    }
}
