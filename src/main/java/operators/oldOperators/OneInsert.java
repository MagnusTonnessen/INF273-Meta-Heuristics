package operators.oldOperators;

import objects.Solution;

import java.util.List;

import static main.Main.problem;
import static utils.Constants.random;
import static utils.Constants.randomRemoval;

public class OneInsert extends Operator {

    @Override
    public Solution operate(Solution solution, int numberOfMoves) {

        List<Integer> calls = randomRemoval.remove(solution, 1);
        int call = calls.get(random.nextInt(calls.size())); // random.nextInt(problem.nCalls);
        List<Integer> validVehicles = problem.calls.get(call).getValidVehicles();

        if (validVehicles.isEmpty()) {
            return solution.copy();
        }

        Solution newSolution = solution.copy();
        newSolution.moveCallRandom(call, validVehicles.get(random.nextInt(validVehicles.size())));
        return newSolution;
    }
}
