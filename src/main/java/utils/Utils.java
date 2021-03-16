package utils;

import objects.Call;
import objects.Results;
import operators.Operators;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.instanceName;
import static utils.PDPUtils.problem;

public class Utils {

    public static final int pad = 20;

    // Print tools
    public static void printRunInfo() {
        System.out.println("\n--- " + instanceName + " ---\n");
        System.out.printf("Initial cost: %.2f\n\n", problem.initialCost);

        System.out.println(
                rightPad("", pad + 25) +
                        rightPad("Average objective", pad) +
                        rightPad("Best objective", pad) +
                        rightPad("Improvement (%)", pad) +
                        rightPad("Running time (seconds)", pad)
        );
    }

    public static void printRunResults(String algorithmName, Results results) {
        DecimalFormat format = new DecimalFormat("0.00#");

        System.out.println("\r" +
                rightPad(algorithmName, pad + 25) +
                rightPad(format.format(results.averageObjective()), pad) +
                rightPad(format.format(results.bestObjective()), pad) +
                rightPad(format.format(results.improvement()), pad) +
                rightPad(format.format(results.averageRunTime()), pad)
        );
    }

    public static String rightPad(String text, int length) {
        return String.format("%1$-" + length + "s", text);
    }

    public static String getAlgorithmName(Method algorithm) {
        if (algorithm.getName().contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.getName().contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.getName().split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getAlgorithmName(String algorithm) {
        if (algorithm.contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getInstanceName(String filePath) {
        return filePath
                .replace("src/main/resources/", "")
                .replace("_", " ")
                .replace(".txt", "");
    }

    // Operator tools
    public static int[] moveCall(int[] solution, int insertIndex, int call) {
        int[] callIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == call).toArray();
        return IntStream.range(0, solution.length).map(i -> {
            if (insertIndex < callIndexes[0] && i >= insertIndex && i <= callIndexes[1]) {
                return i - 1 <= insertIndex ? call : solution[i - ((i - 2) < callIndexes[0] ? 1 : 0) - ((i - 2) < callIndexes[1] ? 1 : 0)];
            } else if (i >= callIndexes[0] && i < insertIndex) {
                return i + 2 >= insertIndex ? call : solution[i + 1 + (i + 1 >= callIndexes[1] ? 1 : 0)];
            }
            return solution[i];
        }).toArray();
    }

    public static int[] movePickup(int[] solution, int insertIndex, int call) {
        int pickupIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == call).sorted().toArray()[0];
        return IntStream.range(0, solution.length).map(i -> {
            if (insertIndex < pickupIndex && i >= insertIndex && i <= pickupIndex) {
                return i == insertIndex ? call : solution[i - ((i - 2) < pickupIndex ? 1 : 0)];
            } else if (i >= pickupIndex && i < insertIndex) {
                return i + 2 == insertIndex ? call : solution[i + 1];
            }
            return solution[i];
        }).toArray();
    }

    public static int[] moveDelivery(int[] solution, int insertIndex, int call) {
        int deliveryIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == call).sorted().toArray()[1];
        return IntStream.range(0, solution.length).map(i -> {
            if (insertIndex < deliveryIndex && i >= insertIndex && i <= deliveryIndex) {
                return i == insertIndex ? call : solution[i - ((i - 2) < deliveryIndex ? 1 : 0)];
            } else if (i >= deliveryIndex && i < insertIndex) {
                return i + 2 == insertIndex ? call : solution[i + 1];
            }
            return solution[i];
        }).toArray();
    }

    public static int[] findFeasibleVehiclePermutation(int[] solution, int vehicleIndex, int call) {

        int[] newSolution = solution.clone();

        if (vehicleIndex == problem.nVehicles) {
            return moveCall(newSolution, newSolution.length - 1, call);
        }

        int vehicleEnd = getVehicleEndIndex(solution, vehicleIndex);
        int vehicleStart = getVehicleStartIndex(solution, vehicleIndex);

        newSolution = moveCall(solution, vehicleEnd, call);

        if (vehicleEnd - vehicleStart == 0) {
            return feasibilityCheck(newSolution) ? newSolution : solution.clone();
        }

        for (int pickupIdx = vehicleStart; pickupIdx < vehicleEnd + 1; pickupIdx++) {
            for (int deliveryIdx = pickupIdx + 1; deliveryIdx < vehicleEnd + 2; deliveryIdx++) {
                newSolution = solution.clone();
                newSolution = movePickup(newSolution, pickupIdx, call);
                newSolution = moveDelivery(newSolution, deliveryIdx, call);
                if (feasibilityCheck(newSolution)) {
                    return newSolution;
                }
            }
        }
        return solution.clone();
    }

    public static int[] findFeasibleInsertIndexes(int[] solution, int call) {
        int[] validVehicles = validVehicleIndexesForCallWithDummy(call);
        return Arrays.stream(validVehicles).filter(vehicleIndex -> {
            int[] newSolution = findFeasibleVehiclePermutation(solution, vehicleIndex, call);
            return !Arrays.equals(newSolution, solution) && feasibilityCheck(newSolution);
        }).toArray();
    }

    public static double percentageTransported(int[] solution) {
        int callsNotTransported = solution.length - getVehicleStartIndex(solution, problem.nVehicles);
        int callsTransported = problem.nCalls - callsNotTransported;
        return (double) callsTransported / problem.nCalls;
    }

    public static int leastExpensiveVehicle(int[] solution, int[] vehicleIndexes) {
        return Arrays.stream(vehicleIndexes).boxed().min(comparingDouble(v -> vehicleCost(solution, v))).get();
    }

    public static int mostExpensiveVehicle(int[] solution) {
        return IntStream.range(0, problem.nVehicles).boxed().max(comparingDouble(v -> vehicleCost(solution, v))).get();
    }

    public static double vehicleCost(int[] solution, int vehicleIndex) {
        return costFunction(getCallsInVehicleWithZeros(solution, vehicleIndex));
    }

    public static int getVehicleEndIndex(int[] solution, int vehicleIndex) {
        return IntStream.rangeClosed(0, solution.length).filter(i -> i == solution.length || solution[i] == 0).toArray()[vehicleIndex];
    }

    public static int getVehicleStartIndex(int[] solution, int vehicleIndex) {
        return vehicleIndex == 0 ? 0 : IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray()[vehicleIndex - 1] + 1;
    }

    public static int[] validCallForVehicle(int vehicleIndex) {
        return problem.vehicleMap.get(vehicleIndex+1).validCalls;
        // int[] calls = problem.vesselCargo[vehicleIndex];
        // return Arrays.stream(calls).filter(call -> calls[call] == 1).toArray();
    }

    public static int[] validVehicleIndexesForCallWithDummy(int call) {
        int[] vehicles = Arrays.stream(problem.vesselCargo).mapToInt(vehicle -> vehicle[call - 1]).toArray();
        return IntStream.rangeClosed(0, vehicles.length).filter(vehicle -> vehicle == vehicles.length || vehicles[vehicle] == 1).toArray();
    }

    public static int[] validVehicleIndexesForCall(int call) {
        int[] vehicles = Arrays.stream(problem.vesselCargo).mapToInt(vehicle -> vehicle[call - 1]).toArray();
        return IntStream.range(0, vehicles.length).filter(vehicle -> vehicles[vehicle] == 1).toArray();
    }

    public static int[] transportedCalls(int[] solution) {
        Set<Integer> notTransported = Arrays.stream(notTransportedCalls(solution)).boxed().collect(toSet());
        return IntStream.rangeClosed(1, problem.nCalls).filter(i -> !notTransported.contains(i)).toArray();
    }

    public static int[] notTransportedCalls(int[] solution) {
        return Arrays.stream(solution).skip(getVehicleStartIndex(solution, problem.nVehicles)).distinct().toArray();
    }

    public static Call[] callsFromID(int[] calls) {
        return Arrays.stream(calls).mapToObj(call -> problem.callsMap.get(call)).toArray(Call[]::new);
    }

    public static int[] getCallsInVehicle(int[] solution, int vehicleIndex) {
        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        return Arrays.stream(solution).skip(vehicleIndex == 0 ? 0 : zeroIndexes[vehicleIndex - 1] + 1).takeWhile(c -> c != 0).toArray();
    }

    public static int[] getCallsInVehicleWithZeros(int[] solution, int vehicleIndex) {
        int startIndex = getVehicleStartIndex(solution, vehicleIndex);
        int endIndex = getVehicleEndIndex(solution, vehicleIndex);
        int[] vehicleWithZeros = new int[endIndex - startIndex + problem.nVehicles];
        System.arraycopy(solution, startIndex, vehicleWithZeros, vehicleIndex, endIndex - startIndex);
        return vehicleWithZeros;
    }

    public static int[] getVehiclesWithAtLeastNCalls(int[] solution, int N) {
        return IntStream.range(0, problem.nVehicles).filter(i -> getVehicleEndIndex(solution, i) - getVehicleStartIndex(solution, i) >= 2 * N).toArray();
    }

    public static int[] getVehiclesWithNToMCalls(int[] solution, int N, int M) {
        return IntStream.range(0, problem.nVehicles).filter(i -> {
            int calls = (getVehicleEndIndex(solution, i) - getVehicleStartIndex(solution, i)) / 2;
            return N <= calls && calls <= M;
        }).toArray();
    }

    public static int getVehicleSize(int[] solution, int vehicleIndex) {
        int startIndex = getVehicleStartIndex(solution, vehicleIndex);
        int endIndex = getVehicleEndIndex(solution, vehicleIndex);
        return endIndex - startIndex;
    }

    public static int[] getEmptyVehicles(int[] solution) {
        return IntStream.range(0, problem.nVehicles).filter(i -> getVehicleSize(solution, i) == 0).toArray();
    }
}
