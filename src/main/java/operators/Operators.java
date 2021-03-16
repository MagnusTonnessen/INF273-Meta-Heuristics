package operators;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;
import static utils.Constants.random;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.problem;
import static utils.PDPUtils.shuffle;
import static utils.Utils.callsFromID;
import static utils.Utils.findFeasibleInsertIndexes;
import static utils.Utils.getCallsInVehicle;
import static utils.Utils.getEmptyVehicles;
import static utils.Utils.getVehicleEndIndex;
import static utils.Utils.getVehicleSize;
import static utils.Utils.getVehicleStartIndex;
import static utils.Utils.getVehiclesWithAtLeastNCalls;
import static utils.Utils.getVehiclesWithNToMCalls;
import static utils.Utils.mostExpensiveVehicle;
import static utils.Utils.moveCall;
import static utils.Utils.notTransportedCalls;
import static utils.Utils.validCallForVehicle;
import static utils.Utils.validVehicleIndexesForCall;
import static utils.Utils.validVehicleIndexesForCallWithDummy;

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
        int[] feasibleInsertIndexes = validVehicleIndexesForCallWithDummy(callToRelocate); // findFeasibleInsertIndexes(solution, callToRelocate);

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

    public static int[] twoExchangeInVehicle(int[] solution) {

        int[] newSolution = solution.clone();

        int[] vehiclesWithTwoCalls = getVehiclesWithAtLeastNCalls(solution, 2);

        for (int vehicleIndex : vehiclesWithTwoCalls) {

            int[] calls = getCallsInVehicle(solution, vehicleIndex);
            int firstCall = calls[random.nextInt(calls.length)];
            int secondCall = calls[random.nextInt(calls.length)];

            if (firstCall == secondCall) {
                continue;
            }

            int[] firstCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == firstCall).toArray();
            int[] secondCallIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == secondCall).toArray();

            newSolution[firstCallIndex[0]] = secondCall;
            newSolution[firstCallIndex[1]] = secondCall;
            newSolution[secondCallIndex[0]] = firstCall;
            newSolution[secondCallIndex[1]] = firstCall;

            return newSolution;
        }
        return newSolution;
    }

    // Move most expensive call from dummy to least expensive valid vehicle
    public static int[] transportAll(int[] solution, double mostExpensiveProbability) {

        int[] newSolution = solution.clone();

        int dummyCallIndex = getVehicleStartIndex(solution, problem.nVehicles);

        if (dummyCallIndex == solution.length) {
            return newSolution;
        }

        int[] calls;
        double P = random.nextDouble();

        if (P > mostExpensiveProbability) {
            calls = notTransportedCalls(solution);
            shuffle(calls);
        } else {
            int[] callsSorted = Arrays.stream(callsFromID(notTransportedCalls(solution)))
                    .sorted(Comparator.comparingInt(call -> -call.costNotTransport))
                    .mapToInt(call -> call.callIndex)
                    .toArray();

            int[] firstThree = Arrays.copyOf(callsSorted, Math.min(3, callsSorted.length));
            shuffle(firstThree);
            System.arraycopy(firstThree, 0, callsSorted, 0, Math.min(3, callsSorted.length));
            calls = callsSorted;
        }

        for (int callToRelocate : calls) {
            int[] validVehicles = validVehicleIndexesForCall(callToRelocate);
            int[] feasibleInsertIndexes = findFeasibleInsertIndexes(solution, callToRelocate);

            if (feasibleInsertIndexes.length < 1) {
                continue;
            }

            int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];
            // int insertIndex = leastExpensiveVehicle(solution, validVehicles);

            return moveCall(solution, insertIndex, callToRelocate);
        }

        return newSolution;
    }

    // Move random call from most expensive vehicle to least expensive valid vehicle
    public static int[] reinsertFromMostExpensiveVehicle(int[] solution) {

        int[] newSolution = solution.clone();

        // Find most expensive vehicle
        int mostExpensiveVehicle = mostExpensiveVehicle(solution);
        int[] calls = getCallsInVehicle(solution, mostExpensiveVehicle);

        if (calls.length < 1) {
            return newSolution;
        }
        // Select random call from vehicle
        int callToRelocate = calls[random.nextInt(calls.length)];

        // Find valid vehicles for that call
        int[] validVehicles = validVehicleIndexesForCall(callToRelocate);

        // Find least expensive vehicle among the valid vehicles
        int leastExpensiveVehicle = validVehicles[random.nextInt(validVehicles.length)]; //leastExpensiveVehicle(solution, validVehicles);

        // If only possible insert vehicle is current vehicle, do nothing
        if (leastExpensiveVehicle == mostExpensiveVehicle) {
            return newSolution;
        }

        // Move call to least expensive vehicle
        return moveCall(solution, getVehicleEndIndex(solution, leastExpensiveVehicle), callToRelocate);
    }

    public static int[] bruteForceVehicle(int[] solution) {

        int[] newSolution = solution.clone();
        int[] bestSolution = solution.clone();
        double bestObjective = costFunction(bestSolution);

        int[] vehicles = getVehiclesWithNToMCalls(solution, 2, 3);

        if (vehicles.length < 1) {
            return newSolution;
        }

        int vehicleIndex = vehicles[random.nextInt(vehicles.length)];
        int vehicleStart = getVehicleStartIndex(solution, vehicleIndex);
        int vehicleEnd = getVehicleEndIndex(solution, vehicleIndex);

        int[] indexes = IntStream.range(0, solution.length).map(i -> vehicleStart).toArray();

        int i = vehicleStart;
        while (i < vehicleEnd) {
            if (indexes[i] < i) {
                if (swap(newSolution, i % 2 == vehicleStart % 2 ? vehicleStart : indexes[i], i)) {
                    double newObjective = costFunction(newSolution);
                    if (newObjective < bestObjective) {
                        bestSolution = newSolution.clone();
                        bestObjective = newObjective;
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
            int[] validCalls = Arrays.stream(validCallForVehicle(vehicleIndex)).filter(notTransported::contains).boxed().sorted(Comparator.comparingInt(call -> validVehicleIndexesForCall(call).length)).mapToInt(i -> i).toArray();
            if (validCalls.length > 0) {
                shuffle(validCalls);
                return moveCall(solution, vehicleIndex, validCalls[0]);
            }
        }
        return solution.clone();
    }
}
