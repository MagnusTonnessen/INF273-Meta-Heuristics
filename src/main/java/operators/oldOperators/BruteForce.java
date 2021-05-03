package operators.oldOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static utils.Constants.random;

/**
 * Get vehicle with 2 to 4 calls,
 * sort from most expensive to least expensive,
 * pick random between 4 most expensive vehicles,
 * test every permutation of calls,
 * if a permutation is cheaper and feasible, return that solution
 */
public class BruteForce extends Operator {
    @Override
    public Solution operate(Solution solution, int numberOfMoves) {

        List<Vehicle> vehicles = solution
                .getVehiclesWithNToMCalls(2, 6)
                .stream()
                .sorted(Comparator.comparingDouble(vehicle -> -vehicle.cost()))
                .collect(Collectors.toList());

        if (vehicles.isEmpty()) {
            return solution.copy();
        }

        Solution newSolution = solution.copy();

        Vehicle bestVehicle = vehicles.get(random.nextInt(Math.min(4, vehicles.size())));
        Vehicle copy = bestVehicle.copy();

        int bestObjective = bestVehicle.cost();

        int[] indexes = new int[bestVehicle.size()];

        int i = 0;
        while (i < copy.size()) {
            if (indexes[i] < i) {
                Collections.swap(copy, i % 2 == 0 ? 0 : indexes[i], i);
                if (copy.isFeasible()) {
                    int newObjective = copy.cost();
                    if (newObjective < bestObjective) {
                        bestObjective = newObjective;
                        bestVehicle = copy;
                    }
                }
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        newSolution.set(bestVehicle.vehicleIndex, bestVehicle);
        return newSolution;
    }
}
