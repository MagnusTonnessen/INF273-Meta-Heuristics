package operators.removalOperators;

import objects.Solution;
import objects.Vehicle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static main.Main.problem;
import static utils.Constants.random;

public class WorstRemoval implements RemovalHeuristic {

    @Override
    public List<Integer> remove(Solution solution, int number) {

        List<Integer> removedCalls = new ArrayList<>();
        Map<Integer, Integer> costMap = new HashMap<>();
        solution.forEach(vehicle -> computeCallCost(vehicle, costMap));

        for (int i = 0; i < number; i++) {
            costMap
                .entrySet()
                .stream()
                .max(Comparator.comparingDouble(e -> -e.getValue()))
                .ifPresent(e -> {
                    Vehicle vehicle = solution.getVehicle(e.getKey());
                    solution.removeCall(e.getKey());
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
