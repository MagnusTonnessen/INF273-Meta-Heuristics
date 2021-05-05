package algorithms;

import objects.Solution;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.oneInsert;
import static utils.Constants.random;
import static utils.Constants.threeExchange;
import static utils.Constants.twoExchange;

public class LocalSearch implements SearchingAlgorithm {
    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {
        return localSearch(iterations, runtime, 0.33, 0.33);
    }

    public Solution localSearch(double iterations, double runtime, double P1, double P2) {
        return localSearch(initialSolution, initialCost, iterations, runtime, P1, P2);
    }

    public Solution localSearch(Solution initialSolution, double initialCost, double iterations, double runtime, double P1, double P2) {

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currentSolution;
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(bestSolution, random.nextInt(3) + 1);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(bestSolution, random.nextInt(3) + 1);
            } else {
                currentSolution = threeExchange.operate(bestSolution, random.nextInt(3) + 1);
            }

            currentCost = currentSolution.cost();

            if (currentSolution.isFeasible() && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
            }
            iteration++;
        }
        return bestSolution;
    }
}
