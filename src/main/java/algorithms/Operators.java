package algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

import static utils.Constants.random;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.problem;
import static utils.PDPUtils.shuffle;

public class Operators {

    public static int[] randomSolution() {
        int nCalls = (int) problem.get("nCalls");
        int nVehicles = (int) problem.get("nVehicles");
        int[] pickup = IntStream.range(1, nCalls + nVehicles + 1).map(i -> (i > nCalls ? 0 : i)).toArray();

        shuffle(pickup);

        int[] solution = new int[2 * nCalls + nVehicles];
        int[] zeroIndex = IntStream.range(0, pickup.length + 1).filter(i -> i >= pickup.length || pickup[i] == 0).toArray();
        int idx = 0;

        for (int i = 0; i < nVehicles + 1; i++) {
            int[] vehicle = Arrays.stream(pickup, idx, zeroIndex[i]).mapToObj(j -> new int[]{j, j}).flatMapToInt(Arrays::stream).toArray();
            shuffle(vehicle);
            System.arraycopy(vehicle, 0, solution, idx * 2 - i, vehicle.length);
            idx = zeroIndex[i] + 1;
        }

        return solution;
    }

    public static int[] oneInsert(int[] solution) {

        int[] newSolution = Arrays.stream(solution).toArray();

        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        int nCalls = (solution.length - zeroIndexes.length) / 2;
        int callToRelocate = random.nextInt(nCalls) + 1;
        int[] feasibleInsertIndexes = findFeasibleInsertIndexes(solution, callToRelocate);

        if (feasibleInsertIndexes.length < 1) {
            return newSolution;
        }

        int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];

        return moveCall(solution, insertIndex, callToRelocate);
    }

    public static int[] twoExchange(int[] solution) {

        int[] newSolution = Arrays.stream(solution).toArray();

        int numVehicles = (int) IntStream.range(0, solution.length).filter(i -> solution[i] == 0).count();
        int nCalls = (solution.length - numVehicles) / 2;
        int firstValue = random.nextInt(nCalls) + 1;
        int secondValue = random.nextInt(nCalls) + 1;

        if (firstValue == secondValue) {
            return newSolution;
        }

        int[] firstValueIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == firstValue).toArray();
        int[] secondValueIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == secondValue).toArray();

        newSolution[firstValueIndex[0]] = secondValue;
        newSolution[firstValueIndex[1]] = secondValue;
        newSolution[secondValueIndex[0]] = firstValue;
        newSolution[secondValueIndex[1]] = firstValue;

        return newSolution;
    }

    public static int[] threeExchange(int[] solution) {

        int[] newSolution = Arrays.stream(solution).toArray();

        int[] zeroIndexes = IntStream.range(0, solution.length + 1).filter(i -> i >= solution.length || solution[i] == 0).toArray();
        int nCalls = (solution.length - zeroIndexes.length + 1) / 2;

        int firstValue = random.nextInt(nCalls) + 1;
        int secondValue = random.nextInt(nCalls) + 1;
        int thirdValue = random.nextInt(nCalls) + 1;

        if (firstValue == secondValue || secondValue == thirdValue || firstValue == thirdValue) {
            return newSolution;
        }

        int[] firstValueIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == firstValue).toArray();
        int[] secondValueIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == secondValue).toArray();
        int[] thirdValueIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == thirdValue).toArray();

        newSolution[firstValueIndex[0]] = thirdValue;
        newSolution[firstValueIndex[1]] = thirdValue;
        newSolution[secondValueIndex[0]] = firstValue;
        newSolution[secondValueIndex[1]] = firstValue;
        newSolution[thirdValueIndex[0]] = secondValue;
        newSolution[thirdValueIndex[1]] = secondValue;

        return newSolution;
    }

    // Not transporting is most expensive
    public static int[] transportAll(int[] solution) {

        int[] newSolution = Arrays.stream(solution).toArray();

        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        int dummyCallIndex = zeroIndexes[zeroIndexes.length - 1] + 1;
        int dummyCallsLength = (solution.length - dummyCallIndex);

        if (dummyCallIndex == solution.length) {
            return newSolution;
        }

        int idx = random.nextInt(dummyCallsLength);
        int callToRelocate = solution[dummyCallIndex + idx];
        int[] feasibleInsertIndexes = findFeasibleInsertIndexes(solution, callToRelocate);

        if (feasibleInsertIndexes.length < 1) {
            return newSolution;
        }

        int insertIndex = feasibleInsertIndexes[random.nextInt(feasibleInsertIndexes.length)];

        return moveCall(solution, insertIndex, callToRelocate);
    }
    // Calls with similar origin node and destination node

    public static int[] similarCalls(int[] solution) {
        return solution;
    }
    // Reduce time calls have to wait for pickup / delivery

    public static int[] reduceWaitTime(int[] solution) {
        return solution;
    }


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

    public static int[] movePickup(int[] solution, int call, int insertIndex) {
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

    public static int[] moveDelivery(int[] solution, int call, int insertIndex) {
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

    public static int[] testAllInserts(int[] solution, int vehicleIndex, int call) {
        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        int vehicleEnd = zeroIndexes[vehicleIndex];
        int vehicleStart = vehicleIndex > 0 ? zeroIndexes[vehicleIndex - 1] + 1: 0;
        for (int index = vehicleStart; index < vehicleEnd; index++) {
            int[] newSolution = moveCall(solution, index, call);
            if (feasibilityCheck(newSolution)) {
                return newSolution;
            }
        }
    }

    public static int[] findFeasibleInsertIndexes(int[] solution, int call) {
        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        return Arrays.stream(zeroIndexes).filter(i -> feasibilityCheck(moveCall(solution, i, call))).toArray();
    }

    public static double percentageTransported(int[] solution) {
        int[] zeroIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray();
        int totalCalls = (solution.length - zeroIndexes.length) / 2;
        int callsNotTransported = (solution.length - (zeroIndexes[zeroIndexes.length - 1] + 1)) / 2;
        int callsTransported = totalCalls - callsNotTransported;
        return (double) callsTransported / totalCalls;
    }

    public static int smallestVehicle(int[] solution) {
        int[] zeroIndexes = IntStream.range(-1, solution.length).filter(i -> i == -1 || solution[i] == 0).toArray();
        return IntStream.range(0, zeroIndexes.length - 1).mapToObj(i -> new int[]{i, zeroIndexes[i + 1] - zeroIndexes[i]}).min(Comparator.comparingInt(v -> v[1])).get()[0];
    }
}
