package algorithms;

import java.util.Map;

import static java.lang.Math.E;
import static java.lang.Math.pow;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.random;

public class SearchingAlgorithms {

    static final int ITERATIONS = 10000;

    // RANDOM SEARCH

    public int[] randomSearch(int[] initialSolution, Map<String, Object> problem) {

        int[] bestSolution = initialSolution;
        double bestCost = costFunction(bestSolution, problem);

        int[] currentSolution;
        double currentCost;

        for (int i = 0; i < ITERATIONS; i++) {

            currentSolution = NeighboursOperators.randomSolution(problem);
            currentCost = costFunction(currentSolution, problem);

            if (feasibilityCheck(currentSolution, problem) && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
            }
        }
        return bestSolution;
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

        for (int i = 0; i < ITERATIONS; i++) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = NeighboursOperators.twoExchange(bestSolution);
            } else if (p < P1 + P2) {
                currentSolution = NeighboursOperators.threeExchange(bestSolution);
            } else {
                currentSolution = NeighboursOperators.oneInsert(bestSolution);
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
        return simulatedAnnealing(initialSolution, problem, 0.33, 0.33, 100, 0.999);
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
        double p;

        for (int i = 0; i < ITERATIONS; i++) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = NeighboursOperators.twoExchange(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = NeighboursOperators.threeExchange(incumbentSolution);
            } else {
                currentSolution = NeighboursOperators.oneInsert(incumbentSolution);
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
