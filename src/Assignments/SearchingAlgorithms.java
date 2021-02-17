package Assignments;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import static Assignments.NeighboursOperators.*;
import static Utilities.PDPUtils.*;

import static java.lang.Math.E;
import static java.lang.Math.pow;

public class SearchingAlgorithms {

    // BLIND RANDOM SEARCH

    public int[] blindRandomSearch(int[] initialSolution, Map<String, Object> problem) {
        int[] bestSolution = initialSolution;
        double bestCost = costFunction(bestSolution, problem);
        for (int i = 0; i < 10000; i++) {
            int[] currentSolution = validRandomSolution(problem);
            double currentCost = costFunction(currentSolution, problem);
            if (feasibilityCheck(currentSolution, problem) && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
            }
        }
        return bestSolution;
    }

    public int[] validRandomSolution(Map<String, Object> problem) {
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

    // LOCAL SEARCH

    public int[] localSearch(int[] initialSolution, Map<String, Object> problem) {
        return localSearch(initialSolution, problem, 0.33, 0.33);
    }

    public int[] localSearch(int[] initialSolution, Map<String, Object> problem, double P1, double P2) {

        int[] bestSolution = initialSolution;
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
            }
        }
        return bestSolution;
    }

    // SIMULATED ANNEALING

    public int[] simulatedAnnealing(int[] initialSolution, Map<String, Object> problem) {
        return simulatedAnnealing(initialSolution, problem, 0.33, 0.33, 100, 0.99);
    }

    public int[] simulatedAnnealing(int[] initialSolution, Map<String, Object> problem, double P1, double P2, double T0, double a) {
        int[] incumbentSolution = initialSolution;
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
                currentSolution = twoExchange(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = threeExchange(incumbentSolution);
            } else {
                currentSolution = oneInsert(incumbentSolution);
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
}
