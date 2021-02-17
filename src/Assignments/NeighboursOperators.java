package Assignments;

import java.util.Arrays;
import java.util.stream.IntStream;

import static Utilities.PDPUtils.*;

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

        int[] zeroIndex = IntStream.range(0, newSolution.length).filter(i -> solution[i] == 0).toArray();
        int idx = random.nextInt(zeroIndex.length);
        int endIdx = zeroIndex[idx];
        int startIdx = idx > 0 ? zeroIndex[idx - 1] + 1 : 0;

        if (endIdx - startIdx > 3) {
            int swapIdx1 = startIdx + random.nextInt(endIdx - startIdx - 1);
            int swapIdx2 = startIdx+ random.nextInt(endIdx - startIdx - 1);
            int swapValue1 = newSolution[swapIdx1];
            int swapValue2 = newSolution[swapIdx2];
            if (swapValue1 != swapValue2) {
                newSolution[swapIdx1] = swapValue2;
                newSolution[swapIdx2] = swapValue1;
            }
        }
        return newSolution;
    }

    public static int[] threeExchange(int[] solution) {
        int[] newSolution = Arrays.stream(solution).toArray();

        int[] zeroIndex = IntStream.range(0, newSolution.length).filter(i -> solution[i] == 0).toArray();
        int idx = random.nextInt(zeroIndex.length);
        int endIdx = zeroIndex[idx];
        int startIdx = idx > 0 ? zeroIndex[idx - 1] + 1 : 0;

        if (endIdx - startIdx > 5) {
            int swapIdx1 = startIdx + random.nextInt(endIdx - startIdx - 1);
            int swapIdx2 = startIdx + random.nextInt(endIdx - startIdx - 1);
            int swapIdx3 = startIdx + random.nextInt(endIdx - startIdx - 1);
            int swapValue1 = newSolution[swapIdx1];
            int swapValue2 = newSolution[swapIdx2];
            int swapValue3 = newSolution[swapIdx3];
            if (swapValue1 != swapValue2 && swapValue1 != swapValue3 && swapValue2 != swapValue3) {
                newSolution[swapIdx1] = swapValue3;
                newSolution[swapIdx2] = swapValue1;
                newSolution[swapIdx3] = swapValue2;
            }
        }
        return newSolution;
    }
}
