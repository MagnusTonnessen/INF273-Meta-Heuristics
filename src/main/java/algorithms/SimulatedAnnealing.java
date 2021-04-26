package algorithms;

import objects.Solution;
import operators.oldOperators.OneInsert;
import operators.oldOperators.Operator;
import operators.oldOperators.ThreeExchange;
import operators.oldOperators.TwoExchange;

import static java.lang.Math.E;
import static java.lang.Math.pow;
import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.random;

public class SimulatedAnnealing implements SearchingAlgorithm {
    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {
        return simulatedAnnealing(iterations, runtime, 0.33, 0.33, 200, 0.999);
    }

    public Solution simulatedAnnealing(int iterations, double runtime, double P1, double P2, double T0, double a) {

        Operator oneInsert = new OneInsert();
        Operator twoExchange = new TwoExchange();
        Operator threeExchange = new ThreeExchange();

        Solution incumbentSolution = initialSolution;
        double incumbentCost = initialCost;

        Solution bestSolution = incumbentSolution.copy();
        double bestCost = incumbentCost;

        Solution currentSolution;
        double currentCost;

        double T = T0;
        double deltaE;
        double p;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(incumbentSolution, random.nextInt(4) + 1);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(incumbentSolution, random.nextInt(4) + 1);
            } else {
                currentSolution = threeExchange.operate(incumbentSolution, random.nextInt(4) + 1);
            }

            currentCost = currentSolution.cost();

            deltaE = currentCost - incumbentCost;

            boolean isFeasible = currentSolution.isFeasible();

            if (isFeasible && deltaE < 0) {
                incumbentSolution = currentSolution;
                incumbentCost = currentCost;

                if (incumbentCost < bestCost) {
                    bestSolution = incumbentSolution;
                    bestCost = incumbentCost;
                }
            } else if (isFeasible && random.nextDouble() < pow(E, -deltaE / T)) {
                incumbentSolution = currentSolution;
                incumbentCost = currentCost;
            }
            T *= a;
            iteration++;
        }
        return bestSolution;
    }
}
