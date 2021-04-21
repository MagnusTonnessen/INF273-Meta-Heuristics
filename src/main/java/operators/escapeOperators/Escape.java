package operators.escapeOperators;

import objects.Solution;
import objects.Vehicle;
import operators.oldOperators.Operator;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;

public class Escape extends Operator {
    @Override
    public Solution operate(Solution solution) {
        solution = new Solution(solution);
        int i = 0;
        while (i < 20) {
            Solution newSolution = new Solution(solution);
            int call = random.nextInt(problem.nCalls);
            List<Vehicle> validVehicles = problem.getCallFromIndex(call).getValidVehicles();
            newSolution.moveCalls(call, validVehicles.get(random.nextInt(validVehicles.size())));
            if (newSolution.isFeasible()) {
                solution = newSolution;
                i++;
            }
        }

        return solution;
    }
}
