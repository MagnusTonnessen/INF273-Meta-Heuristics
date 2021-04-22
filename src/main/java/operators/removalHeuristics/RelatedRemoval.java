package operators.removalHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.Main.problem;

public class RelatedRemoval implements RemovalHeuristic {
    @Override
    public List<Integer> remove(Solution solution, int number) {

        Solution solutionCopy = solution.copy();
        List<Integer> removedCalls = new ArrayList<>();
        Map<Integer, Integer> costMap = new HashMap<>();
        solutionCopy.forEach(vehicle -> computeCallCost(vehicle, costMap));

        for (int i = 0; i < number; i++) {
            costMap
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingDouble(e -> -e.getValue()))
                    .ifPresent(e -> {
                        Vehicle vehicle = solutionCopy.getVehicle(e.getKey());
                        solutionCopy.removeCall(e.getKey());
                        removedCalls.add(e.getKey());
                        costMap.remove(e.getKey());
                        computeCallCost(vehicle, costMap);
                    });
        }
        return removedCalls;
        /*
        Collections.shuffle(removedCalls);
        return removedCalls.subList(0, Math.min(number, removedCalls.size()));
        */
    }

    private void computeCallCost(Vehicle vehicle, Map<Integer, Integer> callCost) {
        if (vehicle.isDummy) {
            vehicle.stream().distinct().forEach(call -> callCost.put(call, problem.getCallFromIndex(call).costNotTransport));
        } else {
            int cost = vehicle.cost();
            for (int call : vehicle.stream().distinct().collect(Collectors.toList())) {
                Vehicle copy = vehicle.copy();
                copy.removeCall(call);
                callCost.put(call, cost - copy.cost());
            }
        }
    }
}
