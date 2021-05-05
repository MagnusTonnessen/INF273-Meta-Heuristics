package operators.insertionHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static main.Main.problem;

public class RegretKInsertion implements InsertionHeuristic {

    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        int k = calls.size();
        List<RegretK> bestInserts = calls.stream().map(call -> bestInserts(solution, call, calls.size())).collect(Collectors.toList());

        while (!bestInserts.isEmpty()) {
            RegretK call = Collections.max(bestInserts, RegretK::compareTo);
            solution.moveCall(call.call(), call.vehicleIndex(), call.insertIndex1(), call.insertIndex2());
            bestInserts.removeIf(c -> c.call() == call.call());
            k--;
            for (int i = 0; i < bestInserts.size(); i++) {
                RegretK rk = bestInserts.get(i);
                if (rk.vehicleIndex == call.vehicleIndex) {
                    bestInserts.set(i, bestInserts(solution, rk.call, k));
                }
            }
        }

        return solution;
    }

    private RegretK bestInserts(Solution solution, int call, int k) {
        List<VehicleInsertCost> minCosts = new ArrayList<>() {
            @Override
            public boolean remove(Object o) {
                if (!(o instanceof VehicleInsertCost)) {
                    return false;
                }
                for (VehicleInsertCost vic : this) {
                    if (vic.cost == (int) o) {
                        return super.remove(vic);
                    }
                }
                return false;
            }
        };
        int maxCost = problem.calls.get(call).costNotTransport;
        int minCost = problem.calls.get(call).costNotTransport;
        int vehicleIndex = solution.getDummy().vehicleIndex;
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
                                minCosts.add(new VehicleInsertCost(cost, copy.vehicleIndex));
                            } else if (cost < maxCost) {
                                minCosts.remove((Integer) maxCost);
                                minCosts.add(new VehicleInsertCost(cost, copy.vehicleIndex));
                                maxCost = Collections.max(minCosts, VehicleInsertCost::compareTo).cost;
                            }
                            if (cost < minCost) {
                                minCost = cost;
                                vehicleIndex = copy.vehicleIndex;
                                insertIndex1 = i;
                                insertIndex2 = j;
                            }
                        }
                    }
                }
            }
        }
        if (minCosts.isEmpty()) {
            minCosts.add(new VehicleInsertCost(problem.calls.get(call).costNotTransport, solution.getDummy().vehicleIndex));
        }
        return new RegretK(call, vehicleIndex, insertIndex1, insertIndex2, minCosts, regretValue(minCosts));
    }

    private int regretValue(List<VehicleInsertCost> costs) {
        int min = Collections.min(costs).cost;
        return costs.stream().mapToInt(v -> v.cost - min).sum();
    }

    private static record RegretK(int call, int vehicleIndex, int insertIndex1, int insertIndex2,
                                  List<VehicleInsertCost> costs, int regretValue) implements Comparable<RegretK> {
        @Override
        public int compareTo(RegretK o) {
            return this.costs.size() < o.costs.size() ? 1 :
                    this.costs.size() > o.costs.size() ? -1 :
                            Integer.compare(this.regretValue, o.regretValue);
        }
    }

    private static record VehicleInsertCost(int vehicleIndex, int cost) implements Comparable<VehicleInsertCost> {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VehicleInsertCost that)) return false;

            if (vehicleIndex != that.vehicleIndex) return false;
            return cost == that.cost;
        }

        @Override
        public int hashCode() {
            int result = vehicleIndex;
            result = 31 * result + cost;
            return result;
        }

        @Override
        public int compareTo(VehicleInsertCost o) {
            return Integer.compare(this.cost, o.cost);
        }
    }
}
