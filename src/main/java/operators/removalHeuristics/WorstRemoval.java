package operators.removalHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static main.Main.problem;

public class WorstRemoval implements RemovalHeuristic {

    @Override
    public List<Integer> remove(Solution solution, int number) {

        Solution solutionCopy = solution.copy();
        List<Integer> removedCalls = new ArrayList<>();
        Map<Integer, Integer> costMap = new HashMap<>();
        solutionCopy.forEach(vehicle -> computeCallCost(vehicle, costMap));

        while (!costMap.isEmpty() && removedCalls.size() < number) {
            int mostExpensiveCall = Collections.max(costMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            Vehicle vehicle = solutionCopy.getVehicle(mostExpensiveCall);
            solutionCopy.removeCall(mostExpensiveCall);
            removedCalls.add(mostExpensiveCall);
            costMap.remove(mostExpensiveCall);
            computeCallCost(vehicle, costMap);
        }
        return removedCalls;
    }

    private void computeCallCost(Vehicle vehicle, Map<Integer, Integer> callCost) {
        if (vehicle.isDummy) {
            vehicle.stream().distinct().forEach(call -> callCost.put(call, problem.getCallFromIndex(call).costNotTransport));
        } else {
            int cost = vehicle.cost();
            Set<Integer> calls = new HashSet<>(vehicle);
            for (int call : calls) {
                Vehicle copy = vehicle.copy();
                copy.removeCall(call);
                callCost.put(call, cost - copy.cost());
            }
        }
    }
}
