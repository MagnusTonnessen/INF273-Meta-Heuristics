package operators.removalOperators;

import objects.Solution;
import objects.Vehicle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static main.Main.problem;
import static utils.Constants.random;

public class WorstRemoval implements RemovalHeuristic {

    @Override
    public List<Integer> remove(Solution solution, int number) {
        return solution
                .stream()
                .filter(vehicle -> vehicle.vehicleIndex != problem.nVehicles)
                .map(vehicle -> {
                    double cost = vehicle.cost();
                    return vehicle
                        .stream()
                        .distinct()
                        .collect(Collectors.toMap(
                            call -> call,
                            call -> {
                                Vehicle copy = vehicle.copy();
                                copy.removeCall(call);
                                return cost - copy.cost();
                            })); })
                .flatMap(e -> e.entrySet().stream())
                .sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .limit(number)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
