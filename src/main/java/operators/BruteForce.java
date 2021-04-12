package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.Collections;
import java.util.List;

import static utils.Constants.random;

public class BruteForce extends Operator {
    @Override
    public Solution operate(Solution solution) {
        Solution newSolution = solution.copy();

        List<Vehicle> vehicles = newSolution.getVehiclesWithNToMCalls(2, 4);
        if (vehicles.size() < 1) {
            return newSolution;
        }

        Vehicle vehicle = vehicles.get(random.nextInt(vehicles.size()));

        double bestObjective = vehicle.cost();

        int[] indexes = new int[vehicle.size()];

        int i = 0;
        while (i < vehicle.size()) {
            if (indexes[i] < i) {
                Collections.swap(vehicle, i % 2 == 0 ? 0 : indexes[i], i);
                if (vehicle.isFeasible()) {
                    double newObjective = vehicle.cost();
                    if (newObjective < bestObjective) {
                        bestObjective = newObjective;
                    }
                }
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return newSolution;
    }
}
