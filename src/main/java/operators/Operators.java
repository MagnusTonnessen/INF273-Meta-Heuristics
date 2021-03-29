package operators;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static utils.Constants.random;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.problem;
import static utils.PDPUtils.shuffle;
import static utils.Utils.getCallsInVehicle;
import static utils.Utils.getEmptyVehicles;
import static utils.Utils.getLeastNExpensiveVehicles;
import static utils.Utils.getNMostExpensiveVehicles;
import static utils.Utils.getVehicleEndIndex;
import static utils.Utils.getVehicleStartIndex;
import static utils.Utils.getVehiclesWithNToMCalls;
import static utils.Utils.moveCall;
import static utils.Utils.notTransportedCalls;
import static utils.Utils.validCallForVehicle;
import static utils.Utils.validVehicleIndexesForCall;
import static utils.Utils.validVehiclesForCallWithDummy;

public class Operators {

    public static int[] randomSolution() {
        int[] pickup = IntStream.rangeClosed(1, problem.nCalls + problem.nVehicles).map(i -> (i > problem.nCalls ? 0 : i)).toArray();

        shuffle(pickup);

        int[] solution = new int[2 * problem.nCalls + problem.nVehicles];
        int idx = 0;

        for (int i = 0; i < problem.nVehicles + 1; i++) {
            int[] vehicle = Arrays.stream(pickup, idx, getVehicleEndIndex(pickup, i)).mapToObj(j -> new int[]{j, j}).flatMapToInt(Arrays::stream).toArray();
            shuffle(vehicle);
            System.arraycopy(vehicle, 0, solution, idx * 2 - i, vehicle.length);
            idx = getVehicleEndIndex(pickup, i) + 1;
        }

        return solution;
    }

    public static int[] oneInsert(int[] solution) {

        int callToRelocate = random.nextInt(problem.nCalls) + 1;
        int[] feasibleInsertIndexes = validVehiclesForCallWithDummy(callToRelocate); // findFeasibleInsertIndexes(solution, callToRelocate);

        if (feasibleInsertIndexes.length < 1) {
            return solution.clone();
        }

        int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];

        return moveCall(solution, insertIndex, callToRelocate);
    }

    public static int[] twoExchange(int[] solution) {

        int[] newSolution = solution.clone();

        int firstCall = random.nextInt(problem.nCalls) + 1;
        int secondCall = random.nextInt(problem.nCalls) + 1;

        if (firstCall == secondCall) {
            return newSolution;
        }

        int[] firstCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == firstCall).toArray();
        int[] secondCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == secondCall).toArray();

        newSolution[firstCallIndex[0]] = secondCall;
        newSolution[firstCallIndex[1]] = secondCall;
        newSolution[secondCallIndex[0]] = firstCall;
        newSolution[secondCallIndex[1]] = firstCall;

        return newSolution;
    }

    public static int[] threeExchange(int[] solution) {

        int[] newSolution = solution.clone();

        int firstCall = random.nextInt(problem.nCalls) + 1;
        int secondCall = random.nextInt(problem.nCalls) + 1;
        int thirdCall = random.nextInt(problem.nCalls) + 1;


        if (firstCall == secondCall || secondCall == thirdCall || firstCall == thirdCall) {
            return newSolution;
        }

        int[] firstCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == firstCall).toArray();
        int[] secondCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == secondCall).toArray();
        int[] thirdCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == thirdCall).toArray();

        newSolution[firstCallIndex[0]] = thirdCall;
        newSolution[firstCallIndex[1]] = thirdCall;
        newSolution[secondCallIndex[0]] = firstCall;
        newSolution[secondCallIndex[1]] = firstCall;
        newSolution[thirdCallIndex[0]] = secondCall;
        newSolution[thirdCallIndex[1]] = secondCall;

        return newSolution;
    }

    public static int[] transportAll(int[] solution) {

        // All calls are transported, return solution

        if (getVehicleStartIndex(solution, problem.nVehicles) == solution.length) {
            return solution.clone();
        }

        for (int n = 1; n <= problem.nVehicles; n++) {
            int finalN = n;

            // Get all calls with N valid vehicles

            int[] calls = Arrays.stream(notTransportedCalls(solution)).boxed().filter(call -> validVehicleIndexesForCall(call).length == finalN).mapToInt(i -> i).toArray();
            shuffle(calls);

            // Iterate in random order

            for (int call : calls) {

                // Get all valid vehicles for call

                int[] vehicles = validVehicleIndexesForCall(call);
                if (vehicles.length > 0) {
                    shuffle(vehicles);

                    // Move call to random valid vehicle and return if feasible

                    int[] newSolution = moveCall(solution, getVehicleEndIndex(solution, vehicles[0]), call);
                    if (feasibilityCheck(newSolution)) {
                        return newSolution;
                    }
                }
            }
        }
        return solution.clone();
    }

    // Move random call from most expensive vehicle to least expensive valid vehicle
    public static int[] reinsertFromMostExpensiveVehicle(int[] solution) {

        int[] newSolution = solution.clone();

        // Find most expensive vehicle
        int[] mostExpensiveVehicles = getNMostExpensiveVehicles(solution, 3);
        shuffle(mostExpensiveVehicles);

        for (int vehicle : mostExpensiveVehicles) {

            int[] calls = getCallsInVehicle(solution, vehicle);

            if (calls.length < 1) {
                return newSolution;
            }

            // Select random call from vehicle
            int callToRelocate = calls[random.nextInt(calls.length)];

            // Find valid vehicles for that call
            int[] validVehicles = validVehiclesForCallWithDummy(callToRelocate);

            // Find least expensive vehicle among the valid vehicles
            int[] leastExpensiveVehicles = getLeastNExpensiveVehicles(solution, validVehicles, 3);
            // int leastExpensiveVehicle = leastExpensiveVehicle(solution, validVehicles); //validVehicles[random.nextInt(validVehicles.length)];

            int leastExpensiveVehicle = leastExpensiveVehicles[random.nextInt(leastExpensiveVehicles.length)];

            // If only possible insert vehicle is current vehicle, do nothing
            if (leastExpensiveVehicle == vehicle) {
                return newSolution;
            }

            // Move call to least expensive vehicle
            return moveCall(solution, getVehicleEndIndex(solution, leastExpensiveVehicle), callToRelocate);
        }
        return solution.clone();

        /*
        int mostExpensiveVehicle = mostExpensiveVehicle(solution);
        int[] calls = getCallsInVehicle(solution, mostExpensiveVehicle);

        if (calls.length < 1) {
            return newSolution;
        }

        // Select random call from vehicle
        int callToRelocate = calls[random.nextInt(calls.length)];

        // Find valid vehicles for that call
        int[] validVehicles = validVehiclesForCallWithDummy(callToRelocate);

        // Find least expensive vehicle among the valid vehicles
        int leastExpensiveVehicle = validVehicles[random.nextInt(validVehicles.length)]; //leastExpensiveVehicle(solution, validVehicles);

        // If only possible insert vehicle is current vehicle, do nothing
        if (leastExpensiveVehicle == mostExpensiveVehicle) {
            return newSolution;
        }

        // Move call to least expensive vehicle
        return moveCall(solution, getVehicleEndIndex(solution, leastExpensiveVehicle), callToRelocate);
        */
    }

    public static int[] reinsertFromMostExpensiveVehicl(int[] solution) {

        // Find most expensive vehicle
        int[] mostExpensiveVehicles = getNMostExpensiveVehicles(solution, 3);

        shuffle(mostExpensiveVehicles);

        for (int vehicle : mostExpensiveVehicles) {

            int[] calls = getCallsInVehicle(solution, vehicle);

            if (calls.length < 1) {
                continue;
            }

            shuffle(calls);

            for (int call : calls) {

                // Find valid vehicles for that call
                int[] validVehicles = validVehiclesForCallWithDummy(call);

                // Find least expensive vehicle among the valid vehicles
                int[] leastExpensiveVehicles = getLeastNExpensiveVehicles(solution, validVehicles, 3);
                // int leastExpensiveVehicle = leastExpensiveVehicle(solution, validVehicles); //validVehicles[random.nextInt(validVehicles.length)];

                if (leastExpensiveVehicles.length < 1) {
                    continue;
                }

                for (int insertVehicle : leastExpensiveVehicles) {
                    if (insertVehicle == vehicle) {
                        continue;
                    }
                    int[] newSolution = moveCall(solution, getVehicleEndIndex(solution, insertVehicle), call);
                    if (feasibilityCheck(newSolution)) {
                        return newSolution;
                    }
                }
            }
        }
        return solution.clone();

        /*
        int mostExpensiveVehicle = mostExpensiveVehicle(solution);
        int[] calls = getCallsInVehicle(solution, mostExpensiveVehicle);

        if (calls.length < 1) {
            return newSolution;
        }

        // Select random call from vehicle
        int callToRelocate = calls[random.nextInt(calls.length)];

        // Find valid vehicles for that call
        int[] validVehicles = validVehiclesForCallWithDummy(callToRelocate);

        // Find least expensive vehicle among the valid vehicles
        int leastExpensiveVehicle = validVehicles[random.nextInt(validVehicles.length)]; //leastExpensiveVehicle(solution, validVehicles);

        // If only possible insert vehicle is current vehicle, do nothing
        if (leastExpensiveVehicle == mostExpensiveVehicle) {
            return newSolution;
        }

        // Move call to least expensive vehicle
        return moveCall(solution, getVehicleEndIndex(solution, leastExpensiveVehicle), callToRelocate);
        */
    }

    public static int[] bruteForceVehicle(int[] solution) {

        int[] vehicles = getVehiclesWithNToMCalls(solution, 2, 4);

        if (vehicles.length < 1) {
            return solution.clone();
        }

        int vehicle = vehicles[random.nextInt(vehicles.length)];

        return bruteForceVehicle(solution, vehicle);
    }

    public static int[] bruteForceVehicle(int[] solution, int vehicle) {

        int[] newSolution = solution.clone();
        int[] bestSolution = solution.clone();
        double bestObjective = costFunction(bestSolution);

        int vehicleStart = getVehicleStartIndex(solution, vehicle);
        int vehicleEnd = getVehicleEndIndex(solution, vehicle);

        int[] indexes = IntStream.range(0, solution.length).map(i -> vehicleStart).toArray();

        int i = vehicleStart;
        while (i < vehicleEnd) {
            if (indexes[i] < i) {
                if (swap(newSolution, i % 2 == vehicleStart % 2 ? vehicleStart : indexes[i], i)) {
                    if (feasibilityCheck(newSolution)) {
                        double newObjective = costFunction(newSolution);
                        if (newObjective < bestObjective) {
                            return newSolution;
                            /*
                            bestSolution = newSolution.clone();
                            bestObjective = newObjective;
                            */
                        }
                    }
                }
                indexes[i]++;
                i = vehicleStart;
            } else {
                indexes[i] = vehicleStart;
                i++;
            }
        }
        return bestSolution;
    }

    private static boolean swap(int[] input, int a, int b) {
        if (input[a] == input[b]) {
            return false;
        }
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
        return true;
    }

    public static int[] fillAllVehicles(int[] solution) {

        int[] emptyVehicles = getEmptyVehicles(solution); // Arrays.stream(getEmptyVehicles(solution)).boxed().sorted(Comparator.comparingInt(v -> problem.vehicleMap.get(v+1).validCalls.length)).mapToInt(i -> i).toArray();
        shuffle(emptyVehicles);

        Set<Integer> notTransported = Arrays.stream(notTransportedCalls(solution)).boxed().collect(toSet());

        for (int vehicleIndex : emptyVehicles) {
            Map<Integer, List<Integer>> valid = Arrays.stream(validCallForVehicle(vehicleIndex)).filter(notTransported::contains).boxed().collect(groupingBy(call -> validVehicleIndexesForCall(call).length));
            int[] validCalls = Arrays.stream(validCallForVehicle(vehicleIndex)).filter(notTransported::contains).boxed().sorted(Comparator.comparingInt(call -> validVehicleIndexesForCall(call).length)).mapToInt(i -> i).toArray();
            if (validCalls.length > 0) {
                // shuffle(validCalls);
                int[] newSolution = moveCall(solution, vehicleIndex, validCalls[0]);
                if (feasibilityCheck(newSolution)) {
                    return newSolution;
                }
            }
        }
        return solution.clone();
    }
}
