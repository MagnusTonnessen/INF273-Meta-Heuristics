package operators.escapeOperators;

import objects.Solution;
import operators.Operator;

import static main.Main.problem;
import static utils.Constants.random;

public class EscapeOperator extends Operator {
    @Override
    public Solution operate(Solution solution) {
        solution = new Solution(solution);
        int i = 0;
        while (i < 20) {
            Solution newSolution = new Solution(solution);
            int call = random.nextInt(problem.nCalls);
            int[] validVehicles = problem.getCallFromIndex(call).validVehicles;
            int vehicle = validVehicles[random.nextInt(validVehicles.length)];
            newSolution.moveCalls(call, vehicle);
            if (newSolution.isFeasible()) {
                solution = newSolution;
                i++;
            }
        }

        return solution;
    }
}
