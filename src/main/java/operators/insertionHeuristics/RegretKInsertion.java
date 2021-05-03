package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegretKInsertion implements InsertionHeuristic {
    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        // Calculate
        List<List<InsertInfo>> best = calls.stream().map(call -> bestInserts(solution, call, calls.size())).collect(Collectors.toList());
        best.forEach(System.out::println);
        /*
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

        */
        return solution;
    }

    private List<InsertInfo> bestInserts(Solution solution, int call, int k) {
        List<InsertInfo> bestInserts = new ArrayList<>() {{
            add(new InsertInfo(call, solution.size() - 1, 0, 1, Integer.MAX_VALUE));
        }};
        int worstBestCost = Integer.MAX_VALUE;
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
                            InsertInfo insertInfo = new InsertInfo(call, copy.vehicleIndex, i, j, cost);
                            if (bestInserts.size() < k) {
                                bestInserts.add(insertInfo);
                                if (cost < worstBestCost) {
                                    worstBestCost = cost;
                                }
                            } else if (cost < worstBestCost) {
                                bestInserts.sort(Comparator.comparingDouble(InsertInfo::cost));
                                bestInserts.remove(bestInserts.size() - 1);
                                bestInserts.add(insertInfo);
                                worstBestCost = cost;
                            }
                        }
                    }
                }
            }
        }
        bestInserts.sort(Comparator.comparingDouble(InsertInfo::cost));
        return bestInserts.subList(0, Math.min(k, bestInserts.size()));
    }

    private void methodName(Solution solution, List<Integer> calls) {
        Map<Integer, List<InsertInfo>> inserts = calls.stream().collect(Collectors.toMap(c -> c, c -> new ArrayList<>()));


    }

    private static record InsertInfo(int call, int vehicleIndex, int insertIndex1, int insertIndex2, int cost) {
    }
}
