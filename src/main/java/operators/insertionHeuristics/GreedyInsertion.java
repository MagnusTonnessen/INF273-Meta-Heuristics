package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreedyInsertion {

    public Solution insert(Solution solution, List<Integer> calls) {
        List<Greedy> bestInserts = calls.stream().map(call -> bestInsert(solution, call)).collect(Collectors.toList());
        while (!bestInserts.isEmpty()) {
            bestInserts.stream().min(Comparator.comparingDouble(Greedy::cost)).ifPresent(call -> {

                solution.moveCall(call.call(), call.vehicleIndex(), call.insertIndex1(), call.insertIndex2());
                bestInserts.removeIf(c -> c.call() == call.call());

                for (int i = 0; i < bestInserts.size(); i++) {
                    if (bestInserts.get(i).vehicleIndex == call.vehicleIndex) {
                        bestInserts.set(i, bestInsert(solution, bestInserts.get(i).call()));
                    }
                }
            });
        }
        return solution;
    }

    private Greedy bestInsert(Solution solution, int call) {
        int minCost = Integer.MAX_VALUE;
        Vehicle insertVehicle = solution.getDummy();
        int insertIndex1 = 0;
        int insertIndex2 = 1;
        for (Vehicle vehicle : solution) {
            if (!vehicle.isDummy) {
                int vehicleCost = vehicle.cost();
                for (int i = 0; i < vehicle.size() + 1; i++) {
                    for (int j = i + 1; j < vehicle.size() + 2; j++) {
                        Vehicle copy = vehicle.copy();
                        copy.add(i, call);
                        copy.add(j, call);
                        if (copy.isFeasible()) {
                            int cost = copy.cost() - vehicleCost;
                            if (cost < minCost) {
                                minCost = cost;
                                insertVehicle = copy;
                                insertIndex1 = i;
                                insertIndex2 = j;
                            }
                        }
                    }
                }
            }
        }
        return new Greedy(call, insertVehicle.vehicleIndex, insertIndex1, insertIndex2, minCost);
    }

    private static record Greedy(int call, int vehicleIndex, int insertIndex1, int insertIndex2, int cost) {
    }
}

