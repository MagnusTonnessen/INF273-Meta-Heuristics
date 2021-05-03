package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreedyInsertion implements InsertionHeuristic {
    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        List<InsertInfo> bestInserts = calls.stream().map(call -> bestInsert(solution, call)).collect(Collectors.toList());
        for (int i = 0; i < calls.size(); i++) {

            bestInserts.stream().min(Comparator.comparingDouble(InsertInfo::cost)).ifPresent(bestInsert -> {

                solution.moveCall(bestInsert.call(), bestInsert.vehicleIndex(), bestInsert.insertIndex1(), bestInsert.insertIndex2());
                bestInserts.removeIf(call -> call.call() == bestInsert.call());

                for (int j = 0; j < bestInserts.size(); j++) {
                    if (bestInserts.get(j).vehicleIndex == bestInsert.vehicleIndex) {
                        bestInserts.set(j, bestInsert(solution, bestInserts.get(j).call()));
                    }
                }
            });
        }
        return solution;
    }

    private InsertInfo bestInsert(Solution solution, int call) {
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
        return new InsertInfo(call, insertVehicle.vehicleIndex, insertIndex1, insertIndex2, minCost);
    }

    private static record InsertInfo(int call, int vehicleIndex, int insertIndex1, int insertIndex2, int cost) {
    }
}

