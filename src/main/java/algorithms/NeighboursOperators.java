package algorithms;

import java.util.Arrays;
import java.util.stream.IntStream;

import static utils.Constants.problem;
import static utils.PDPUtils.random;
import static utils.PDPUtils.shuffle;

public class NeighboursOperators {

    public static int[] oneInsert(int[] solution) {

        int[] zeroIndexes = IntStream.range(0, solution.length + 1).filter(i -> i >= solution.length || solution[i] == 0).toArray();
        int nCalls = (solution.length - zeroIndexes.length + 1)/2;
        int valueToRelocate = random.nextInt(nCalls) + 1;
        int[] valueIndexes = IntStream.range(0, solution.length).filter(i -> solution[i] == valueToRelocate).toArray();
        int idx = random.nextInt(zeroIndexes.length);
        int vehicleIdx = zeroIndexes[idx];
        int insertIdx = idx > 0 ? zeroIndexes[idx - 1] + 1 : 0;

        if (insertIdx <= valueIndexes[0] && valueIndexes[0] < vehicleIdx) {
            return solution;
        }

        return IntStream.range(0, solution.length).map(oldIndex -> {
            int newIndex = oldIndex;
            if (valueIndexes[1] < insertIdx && newIndex < insertIdx) {
                if (newIndex >= valueIndexes[0]) { newIndex++; }
                if (newIndex >= valueIndexes[1]) { newIndex++; }
                if (newIndex == insertIdx || newIndex == insertIdx + 1) { return valueToRelocate; }
            } else if (newIndex <= valueIndexes[1]){
                if (newIndex == insertIdx || newIndex == insertIdx + 1) { return valueToRelocate; }
                if (newIndex > insertIdx) { newIndex -= 2; }
                if (newIndex >= valueIndexes[0]) { newIndex++; }
                if (newIndex >= valueIndexes[1]) { newIndex++; }
            }
            return solution[newIndex];
        }).toArray();
    }

    public static int[] twoExchange(int[] solution) {

        int[] newSolution = Arrays.stream(solution).toArray();

        int[] zeroIndexes = IntStream.range(0, solution.length + 1).filter(i -> i >= solution.length || solution[i] == 0).toArray();
        int nCalls = (solution.length - zeroIndexes.length + 1)/2;
        int firstValue = random.nextInt(nCalls) + 1;
        int secondValue = random.nextInt(nCalls) + 1;

        if (firstValue == secondValue) { return newSolution; }

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
        int nCalls = (solution.length - zeroIndexes.length + 1)/2;

        int firstValue = random.nextInt(nCalls) + 1;
        int secondValue = random.nextInt(nCalls) + 1;
        int thirdValue = random.nextInt(nCalls) + 1;

        if (firstValue == secondValue || secondValue == thirdValue || firstValue == thirdValue) { return newSolution; }

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

    public static int[] randomSolution() {
        int nCalls = (int) problem.get("nCalls");
        int nVehicles = (int) problem.get("nVehicles");
        int[] pickup = IntStream.range(1, nCalls + nVehicles + 1).map(i -> (i > nCalls ? 0 : i)).toArray();

        shuffle(pickup);

        int[] solution = new int[2 * nCalls + nVehicles];
        int[] zeroIndex = IntStream.range(0, pickup.length + 1).filter(i -> i >= pickup.length || pickup[i] == 0).toArray();
        int idx = 0;

        for (int i = 0; i < nVehicles + 1; i++) {
            int[] vehicle = Arrays.stream(pickup, idx, zeroIndex[i]).mapToObj(j -> new int[] {j, j}).flatMapToInt(Arrays::stream).toArray();
            shuffle(vehicle);
            System.arraycopy(vehicle, 0, solution, idx * 2 - i, vehicle.length);
            idx = zeroIndex[i] + 1;
        }

        return solution;
    }
}
