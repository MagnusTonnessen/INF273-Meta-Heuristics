package algorithms;

import objects.Solution;
import operators.OneInsert;
import operators.Operator;
import operators.ThreeExchange;
import operators.TwoExchange;

import static java.lang.Math.E;
import static java.lang.Math.pow;
import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.random;

public class SimulatedAnnealing implements SearchingAlgorithm {
    @Override
    public Solution search(double runtime) {
        return simulatedAnnealing(runtime, 0.33, 0.33, 200, 0.999);
    }

    public Solution simulatedAnnealing(double runtime, double P1, double P2, double T0, double a) {

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

        double endTime = System.currentTimeMillis() + runtime * 1000L;
        while (System.currentTimeMillis() < endTime) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(incumbentSolution);
            } else {
                currentSolution = threeExchange.operate(incumbentSolution);
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
        }

        /*
        for (int i = 0; i < ITERATIONS; i++) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = oneInsert.operate(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = twoExchange.operate(incumbentSolution);
            } else {
                currentSolution = threeExchange.operate(incumbentSolution);
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
        }
        */
        return bestSolution;
    }
}
