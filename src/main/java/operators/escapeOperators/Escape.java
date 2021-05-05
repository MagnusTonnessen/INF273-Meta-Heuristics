package operators.escapeOperators;

import objects.Solution;
import objects.Vehicle;
import operators.oldOperators.Operator;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class Escape extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {
        int i = 0;
        while (i < 20) {
            Solution newSolution = new Solution(solution);
            int call = random.nextInt(problem.nCalls);
            Vehicle currentVehicle = newSolution.getVehicleFromCall(call);
            List<Integer> validVehicles = problem.getCallFromIndex(call).getValidVehicles();
            validVehicles.removeIf(vehicle -> vehicle == currentVehicle.vehicleIndex);
            validVehicles.add(newSolution.getDummy().vehicleIndex);
            newSolution.moveCallRandom(call, validVehicles.get(random.nextInt(validVehicles.size())));
            if (newSolution.isFeasible()) {
                solution = newSolution;
                i++;
            }
        }
        return solution;
    }
}
