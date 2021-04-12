package operators.insertionOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static main.Main.problem;

public class GreedyInsertion implements InsertionHeuristic {
    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        Solution solutionCopy = solution.copy();
        for (int call : calls) {
            problem
                // Get valid vehicles for call
                .getCallFromIndex(call)
                .getValidVehicles()
                .stream()
                .map(vehicle -> solutionCopy.get(vehicle.vehicleIndex))
                // Insert call at random position for each vehicle
                .map(vehicle -> {
                    Vehicle vehicleCopy = vehicle.copy();
                    vehicleCopy.randomInsert(call);
                    return vehicleCopy; })
                .distinct()
                // Remove infeasible vehicles
                .filter(Vehicle::isFeasible)
                // Map each vehicle to its cost
                .collect(toMap(
                    Function.identity(),
                    Vehicle::cost))
                .entrySet()
                .stream()
                // Get vehicle with minimum cost
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                // If a vehicles is present, replace vehicle with the new vehicle, else place call in dummy
                .ifPresentOrElse(e -> solutionCopy.set(e.getKey().vehicleIndex, e.getKey()), () -> solutionCopy.getDummy().randomInsert(call));
        }
        return solutionCopy;
    }

    private Vehicle bestInsert(Vehicle vehicle, int call) {
        Vehicle cheapestInsert = vehicle;
        double minCost = Integer.MAX_VALUE;
        for (int i = 0; i < vehicle.size() - 1; i++) {
            for (int j = 0; j < vehicle.size(); j++) {
                Vehicle copy = vehicle.copy();
                copy.add(i, call);
                copy.add(j, call);
                double cost = vehicle.cost();
                if (cost < minCost) {
                    minCost = cost;
                    cheapestInsert = copy;
                }
            }
        }
        return cheapestInsert;
    }
}
