package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegretKInsertion implements InsertionHeuristic {
    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        List<InsertInfo> best = calls.stream().map(call -> bestInsert(solution, call)).collect(Collectors.toList());
        for (int i = 0; i < calls.size(); i++) {

            best.stream().min(Comparator.comparingDouble(InsertInfo::cost)).ifPresent(bestInsert -> {

                solution.moveCall(bestInsert.call(), bestInsert.vehicleIndex(), bestInsert.insertIndex1(), bestInsert.insertIndex2());
                best.removeIf(call -> call.call() == bestInsert.call());

                for (int j = 0; j < best.size(); j++) {
                    if (best.get(j).vehicleIndex == bestInsert.vehicleIndex) {
                        best.set(j, bestInsert(solution, best.get(j).call()));
                    }
                }
            });

        }
        return solution;
    }

    private InsertInfo bestInsert(Solution solution, int call) {
        double minCost = Integer.MAX_VALUE;
        Vehicle cheapestInsert = solution.getDummy();
        int insert1 = 0;
        int insert2 = 1;
        for (Vehicle vehicle : solution) {
            if (!vehicle.isDummy) {
                double vehicleCost = vehicle.cost();
                for (int i = 0; i < vehicle.size() + 1; i++) {
                    for (int j = i + 1; j < vehicle.size() + 2; j++) {
                        Vehicle copy = vehicle.copy();
                        copy.add(i, call);
                        copy.add(j, call);
                        if (copy.isFeasible()) {
                            double cost = copy.cost() - vehicleCost;
                            if (cost < minCost) {
                                minCost = cost;
                                cheapestInsert = copy;
                                insert1 = i;
                                insert2 = j;
                            }
                        }
                    }
                }
            }
        }
        return new InsertInfo(call, cheapestInsert.vehicleIndex, insert1, insert2, minCost);
    }

    private static record InsertInfo(int call, int vehicleIndex, int insertIndex1, int insertIndex2, double cost) {
    }
}
