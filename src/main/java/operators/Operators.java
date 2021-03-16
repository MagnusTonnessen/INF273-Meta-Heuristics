package operators;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

import static utils.Constants.random;
import static utils.PDPUtils.problem;
import static utils.PDPUtils.shuffle;
import static utils.Utils.callsFromID;
import static utils.Utils.findFeasibleInsertIndexes;
import static utils.Utils.getCallsInVehicle;
import static utils.Utils.getVehicleEndIndex;
import static utils.Utils.getVehicleStartIndex;
import static utils.Utils.getVehiclesWithAtLeastNCalls;
import static utils.Utils.getVehiclesWithNToMCalls;
import static utils.Utils.leastExpensiveVehicle;
import static utils.Utils.mostExpensiveVehicle;
import static utils.Utils.moveCall;
import static utils.Utils.notTransportedCalls;
import static utils.Utils.validVehicleIndexesForCall;

public class Operators {

    public static int[] randomSolution() {
        int[] pickup = IntStream.rangeClosed(1, problem.nCalls + problem.nVehicles).map(i -> (i > problem.nCalls ? 0 : i)).toArray();

        shuffle(pickup);

        int[] solution = new int[2 * problem.nCalls + problem.nVehicles];
        int idx = 0;

        for (int i = 0; i < problem.nVehicles + 1; i++) {
            int[] vehicle = Arrays.stream(pickup, idx, getVehicleEndIndex(solution, i)).mapToObj(j -> new int[]{j, j}).flatMapToInt(Arrays::stream).toArray();
            shuffle(vehicle);
            System.arraycopy(vehicle, 0, solution, idx * 2 - i, vehicle.length);
            idx = getVehicleEndIndex(solution, i) + 1;
        }

        return solution;
    }

    public static int[] oneInsert(int[] solution) {

        int callToRelocate = random.nextInt(problem.nCalls) + 1;
        int[] feasibleInsertIndexes = findFeasibleInsertIndexes(solution, callToRelocate);

        if (feasibleInsertIndexes.length < 1) {
            return solution.clone();
        }

        int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];

        return moveCall(solution, insertIndex, callToRelocate);
    }

    public static int[] bruteForceVehicle(int[] solution, int index) {

        int[] newSolution = solution.clone();

        int[] vehicles = getVehiclesWithNToMCalls(solution, 2, 3);
        int vehicleIndex = index; //random.nextInt(vehicles.length);
        int vehicleStart = getVehicleStartIndex(solution, vehicleIndex);
        int vehicleEnd = getVehicleEndIndex(solution, vehicleIndex);

        int[] indexes = new int[vehicleEnd - vehicleStart];

        int i = vehicleStart;
        while (i < vehicleEnd) {
            if (indexes[i - vehicleStart] < i) {
                swap(newSolution, i % 2 == 0 ? 0 : indexes[i - vehicleStart], i - vehicleStart);
                System.out.println(Arrays.toString(newSolution));
                indexes[i - vehicleStart]++;
                i = vehicleStart;
            } else {
                indexes[i - vehicleStart] = 0;
                i++;
            }
        }
        return newSolution;
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
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

    // Move most expensive call from dummy to least expensive valid vehicle
    public static int[] transportAll(int[] solution) {

        int[] newSolution = solution.clone();

        int dummyCallIndex = getVehicleStartIndex(solution, problem.nVehicles);

        if (dummyCallIndex == solution.length) {
            return newSolution;
        }

        int[] callsSorted = Arrays.stream(callsFromID(notTransportedCalls(solution)))
                .sorted(Comparator.comparingInt(call -> -call.costNotTransport))
                .mapToInt(call -> call.callIndex)
                .toArray();

        int[] firstThree = Arrays.copyOf(callsSorted, 3);
        shuffle(firstThree);
        System.arraycopy(firstThree, 0, callsSorted, 0, 3);

        for (int callToRelocate : callsSorted) {
            int[] validVehicles = validVehicleIndexesForCall(callToRelocate);
            int[] feasibleInsertIndexes = findFeasibleInsertIndexes(solution, callToRelocate);

            if (validVehicles.length < 1) {
                continue;
            }

            // int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];
            int insertIndex = leastExpensiveVehicle(solution, validVehicles);

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
        int leastExpensiveVehicle = leastExpensiveVehicle(solution, validVehicles);

        // If only possible insert vehicle is current vehicle, do nothing
        if (leastExpensiveVehicle == mostExpensiveVehicle) {
            return newSolution;
        }

        // Move call to least expensive vehicle
        return moveCall(solution, getVehicleEndIndex(solution, leastExpensiveVehicle), callToRelocate);
    }

    // Calls with similar origin node and destination node
    public static int[] similarCalls(int[] solution) {
        return solution;
    }

    // Reduce time calls have to wait for pickup / delivery
    public static int[] reduceWaitTime(int[] solution) {
        return solution;
    }
}
