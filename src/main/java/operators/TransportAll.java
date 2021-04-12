package operators;

import objects.Solution;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static main.Main.problem;

public class TransportAll extends Operator {

    @Override
    public Solution operate(Solution solution) {
        // All calls are transported, return solution

        if (solution.getDummy().isEmpty()) {
            return solution.copy();
        }

        for (int n = 0; n < problem.nVehicles; n++) {
            int finalN = n;

            // Get all calls with N valid vehicles

            List<Integer> calls = solution.getDummy().stream().filter(call -> problem.calls.get(call).validVehicles.length == finalN).distinct().collect(toList());
            Collections.shuffle(calls);

            // Iterate in random order

            for (int call : calls) {

                // Get all valid vehicles for call

                List<Integer> vehicles = Arrays.stream(problem.calls.get(call).validVehicles).boxed().collect(toList());
                if (vehicles.size() > 0) {
                    Collections.shuffle(vehicles);

                    // Move call to random valid vehicle and return if feasible

                    Solution newSolution = solution.copy();
                    newSolution.moveCalls(call, vehicles.get(0));
                    if (newSolution.isFeasible()) {
                        return newSolution;
                    }
                }
            }
        }
        return solution.copy();
    }
}
