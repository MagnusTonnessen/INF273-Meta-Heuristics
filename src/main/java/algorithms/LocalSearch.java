package algorithms;

import objects.Solution;
import operators.oldOperators.OneInsert;
import operators.oldOperators.Operator;
import operators.oldOperators.ThreeExchange;
import operators.oldOperators.TwoExchange;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.random;

public class LocalSearch implements SearchingAlgorithm {
    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {
        return localSearch(iterations, runtime, 0.33, 0.33);
    }

    public Solution localSearch(Solution initialSolution, double initialCost, double iterations, double runtime, double P1, double P2) {
        Operator oneInsert = new OneInsert();
        Operator twoExchange = new TwoExchange();
        Operator threeExchange = new ThreeExchange();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currentSolution;
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(bestSolution);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(bestSolution);
            } else {
                currentSolution = threeExchange.operate(bestSolution);
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

    public Solution localSearch(double iterations, double runtime, double P1, double P2) {
        Operator oneInsert = new OneInsert();
        Operator twoExchange = new TwoExchange();
        Operator threeExchange = new ThreeExchange();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currentSolution;
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            double p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(bestSolution);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(bestSolution);
            } else {
                currentSolution = threeExchange.operate(bestSolution);
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
