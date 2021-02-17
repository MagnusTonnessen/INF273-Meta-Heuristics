package Assignments;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import static Utils.PDPUtils.costFunction;
import static Utils.PDPUtils.feasibilityCheck;
import static Utils.PDPUtils.random;
import static java.lang.Math.E;
import static java.lang.Math.pow;

public class Assignment3 {

    public int[] localSearch(int[] initSolution, Map<String, Object> problem) {
        return localSearch(initSolution, problem, 0.33, 0.33);
    }

    public int[] localSearch(int[] initSolution, Map<String, Object> problem, double P1, double P2) {

        int[] bestSolution = initSolution;
        double bestCost = costFunction(bestSolution, problem);

        int[] currentSolution;
        double currentCost;

        for (int i = 0; i < 10000; i++) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = twoExchange(bestSolution);
            } else if (p < P1 + P2) {
                currentSolution = threeExchange(bestSolution);
            } else {
                currentSolution = oneInsert(bestSolution);
            }

            currentCost = costFunction(currentSolution, problem);

            if (feasibilityCheck(currentSolution, problem) && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
                System.out.println("Best sol: " + Arrays.toString(bestSolution));
                System.out.println("Best cost: " + bestCost + "\n");
            }
        }
        return bestSolution;
    }

    public int[] simulatedAnnealing(int[] initSolution, Map<String, Object> problem, double P1, double P2, double T0, double a) {
        int[] incumbentSolution = initSolution;
        double incumbentCost = costFunction(incumbentSolution, problem);

        int[] bestSolution = incumbentSolution;
        double bestCost = incumbentCost;

        int[] currentSolution;
        double currentCost;

        double T = T0;
        double deltaE;

        for (int i = 0; i < 10000; i++) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = null; // 2-exchange on incumbentSolution
            } else if (p < P1 + P2) {
                currentSolution = null; // 3-exchange on incumbentSolution
            } else {
                currentSolution = null; // 1-insert on incumbentSolution
            }

            currentCost = costFunction(currentSolution, problem);

            deltaE =  currentCost - incumbentCost;

            boolean currentFeasible = feasibilityCheck(currentSolution, problem);

            if (currentFeasible && deltaE < 0) {
                incumbentSolution = currentSolution;
                incumbentCost = currentCost;

                if (incumbentCost < bestCost) {
                    bestSolution = incumbentSolution;
                    bestCost = incumbentCost;
                }
            } else if (currentFeasible && random.nextDouble() < pow(E, -deltaE / T)) {
                incumbentSolution = currentSolution;
                incumbentCost = currentCost;
            }
            T *= a;
        }
        return bestSolution;
    }

    public int[] oneInsert(int[] solution) {
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

        return IntStream.range(0, solution.length).map(i -> getValue(solution, i, valueIndexes, insertIdx, valueToRelocate)).toArray();
    }

    public int getValue(int[] solution, int index, int[] valueIndexes, int insertIdx, int valueToRelocate) {
        if (valueIndexes[1] < insertIdx && index < insertIdx) {
            if (index >= valueIndexes[0]) { index++; }
            if (index >= valueIndexes[1]) { index++; }
            if (index == insertIdx || index == insertIdx + 1) { return valueToRelocate; }
        } else if (index <= valueIndexes[1]){
            if (index == insertIdx || index == insertIdx + 1) { return valueToRelocate; }
            if (index > insertIdx) { index -= 2; }
            if (index >= valueIndexes[0]) { index++; }
            if (index >= valueIndexes[1]) { index++; }
        }
        return solution[index];
    }

    public int[] twoExchange(int[] solution) {

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

    public int[] threeExchange(int[] solution) {
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
