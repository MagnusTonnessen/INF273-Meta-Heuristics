package algorithms;

import objects.Solution;
import operators.OneInsert;
import operators.Operator;
import operators.ThreeExchange;
import operators.TwoExchange;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;
import static utils.Constants.random;

public class LocalSearch implements SearchingAlgorithm {
    @Override
    public Solution search() {
        return localSearch(0.50, 0.25);
    }

    public Solution localSearch(double P1, double P2) {
        Operator oneInsert = new OneInsert();
        Operator twoExchange = new TwoExchange();
        Operator threeExchange = new ThreeExchange();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        Solution currentSolution;
        double currentCost;

        for (int i = 0; i < ITERATIONS; i++) {

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
        }
        return bestSolution;
    }
}
