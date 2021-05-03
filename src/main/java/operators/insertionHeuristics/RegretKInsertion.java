package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static main.Main.problem;

public class RegretKInsertion implements InsertionHeuristic {

    private final Comparator<RegretK> CMP = new RegretKCmp();

    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        AtomicInteger k = new AtomicInteger(calls.size());
        List<RegretK> bestInserts = calls.stream().map(call -> bestInserts(solution, call, k.get())).collect(Collectors.toList());

        while (!bestInserts.isEmpty()) {
            RegretK call = Collections.max(bestInserts, CMP);
            solution.moveCall(call.call(), call.vehicleIndex(), call.insertIndex1(), call.insertIndex2());
            bestInserts.removeIf(c -> c.call() == call.call());
            k.decrementAndGet();
            bestInserts = bestInserts.stream().map(e -> bestInserts(solution, e.call, k.get())).collect(Collectors.toList());
            // bestInserts = bestInserts.parallelStream().map(e -> bestInserts(solution, e.call, k.get())).collect(Collectors.toList());
        }

        return solution;
    }

    private static class RegretKCmp implements Comparator<RegretK> {

        @Override
        public int compare(RegretK o1, RegretK o2) {
            return o1.costs.size() < o2.costs.size() ? 1 :
                    o1.costs.size() > o2.costs.size() ? -1 :
                    Integer.compare(o1.regretValue, o2.regretValue);
        }
    }

    private RegretK bestInserts(Solution solution, int call, int k) {
        List<Integer> minCosts = new ArrayList<>();
        int maxCost = problem.calls.get(call).costNotTransport;
        int minCost = problem.calls.get(call).costNotTransport;
        Vehicle insertVehicle = solution.getDummy();
        int insertIndex1 = 0;
        int insertIndex2 = 1;
        for (Vehicle vehicle : solution) {
            if (!vehicle.isDummy) {
                int vehicleCost = vehicle.cost();
                for (int i = 0; i < vehicle.size() + 1; i++) {
                    for (int j = i + 1; j < vehicle.size() + 2; j++) {
                        Vehicle copy = vehicle.copy();
                        copy.insertCall(call, i, j);
                        if (copy.isFeasible()) {
                            int cost = copy.cost() - vehicleCost;
                            if (minCosts.size() < k) {
                                minCosts.add(cost);
                            } else if (cost < maxCost) {
                                minCosts.remove((Integer) maxCost);
                                minCosts.add(cost);
                                maxCost = Collections.max(minCosts);
                            }
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
        if (minCosts.isEmpty()) {
            minCosts.add(problem.calls.get(call).costNotTransport);
        }
        return new RegretK(call, insertVehicle.vehicleIndex, insertIndex1, insertIndex2, minCosts, regretValue(minCosts));
    }

    private int regretValue(List<Integer> costs) {
        int min = Collections.min(costs);
        return costs.stream().mapToInt(cost -> cost - min).sum();
    }

    private static record RegretK(int call, int vehicleIndex, int insertIndex1, int insertIndex2, List<Integer> costs, int regretValue) {
    }
}
