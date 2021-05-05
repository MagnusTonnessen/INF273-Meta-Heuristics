package operators.removalHeuristics;

import objects.Solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static main.Main.problem;
import static utils.Constants.random;

public class RelatedRemoval implements RemovalHeuristic {

    private double[][] relations;

    @Override
    public List<Integer> remove(Solution solution, int number) {
        return solution
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list;
                        }))
                .stream()
                .limit(number)
                .collect(Collectors.toList());
    }

    public List<Integer> remove_(Solution solution, int number) {
        List<Integer> removedCalls = new ArrayList<>();
        removedCalls.add(random.nextInt(problem.nCalls));
        while (removedCalls.size() < number) {
            int removedCall = removedCalls.get(random.nextInt(removedCalls.size()));
            List<Integer> relatedCalls = getMostRelatedCallsSorted(removedCall);
            for (int call : relatedCalls) {
                if (!removedCalls.contains(call)) {
                    removedCalls.add(call);
                    break;
                }
            }
        }
        return null;
    }

    public List<Integer> getMostRelatedCallsSorted(int call) {
        return IntStream.range(0, problem.nCalls).filter(c -> c != call).boxed().sorted(Comparator.comparingDouble(c -> relations[call][c])).collect(Collectors.toList());
    }

    public void calculateRelations() {
        double[][] relations = new double[problem.nCalls][problem.nCalls];
        for (int call1 = 0; call1 < problem.nCalls - 1; call1++) {
            for (int call2 = call1 + 1; call2 < problem.nCalls; call2++) {
                relations[call1][call2] = relations[call2][call1] = getRelation(call1, call2);
            }
        }
        this.relations = relations;
    }

    private double getRelation(int call1, int call2) {
        return normalize(4, 0,
                getDistanceRelation(call1, call2) +
                        getTimeRelation(call1, call2) +
                        getSizeRelation(call1, call2) +
                        getVesselCompatibilityRelation(call1, call2));
    }

    private double getDistanceRelation(int call1, int call2) {
        List<Integer> commonVehicles = getCommonVehicles(call1, call2);
        if (commonVehicles.isEmpty()) {
            return 1;
        }
        int org1 = problem.calls.get(call1).originNode;
        int org2 = problem.calls.get(call2).originNode;
        int dest1 = problem.calls.get(call1).destinationNode;
        int dest2 = problem.calls.get(call2).destinationNode;
        int org1ToOrg2 = commonVehicles.stream().reduce(0, (acc, vehicle) -> problem.travelTime[vehicle][org1][org2], Integer::sum);
        // int org1ToDest2 = commonVehicles.stream().reduce(0, (acc, vehicle) -> problem.travelTime[vehicle][org1][dest2], Integer::sum);
        // int org2ToDest1 = commonVehicles.stream().reduce(0, (acc, vehicle) -> problem.travelTime[vehicle][org2][dest1], Integer::sum);
        int dest1ToDest2 = commonVehicles.stream().reduce(0, (acc, vehicle) -> problem.travelTime[vehicle][dest1][dest2], Integer::sum);
        return normalize(1, 0, org1ToOrg2 + dest1ToDest2);
    }

    private double getTimeRelation(int call1, int call2) {
        int PL1 = problem.calls.get(call1).lowerTimePickup;
        int PL2 = problem.calls.get(call2).lowerTimePickup;
        int PU1 = problem.calls.get(call1).upperTimePickup;
        int PU2 = problem.calls.get(call2).upperTimePickup;
        int DL1 = problem.calls.get(call1).lowerTimeDelivery;
        int DL2 = problem.calls.get(call2).lowerTimeDelivery;
        int DU1 = problem.calls.get(call1).upperTimeDelivery;
        int DU2 = problem.calls.get(call2).upperTimeDelivery;

        int pickupWindow = Math.min(PU1, PU2) - Math.max(PL1, PL2);
        int deliveryWindow = Math.min(DU1, DU2) - Math.max(DL1, DL2);

        return 0.0;
    }

    private double getSizeRelation(int call1, int call2) {
        return normalize(problem.maxCallSize - problem.minCallSize, 0, Math.abs(problem.calls.get(call1).size - problem.calls.get(call2).size));
    }

    private double getVesselCompatibilityRelation(int call1, int call2) {
        double minSize = min(problem.calls.get(call1).validVehicles.size(),
                problem.calls.get(call2).validVehicles.size());
        double commonVehicles = getCommonVehicles(call1, call2).size();
        return normalize(1, 0, 1 - (commonVehicles / minSize));
    }

    private double normalize(double maxX, double minX, double X) {
        return maxX == minX ? 0.5 : (X - minX) / (maxX - minX);
    }

    private List<Integer> getCommonVehicles(int call1, int call2) {
        List<Integer> v1 = new ArrayList<>(problem.calls.get(call1).validVehicles);
        v1.retainAll(problem.calls.get(call2).validVehicles);
        return v1;
    }
}
