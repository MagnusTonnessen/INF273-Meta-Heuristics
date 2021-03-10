package algorithms;

import static algorithms.Operators.oneInsert;
import static algorithms.Operators.randomSolution;
import static algorithms.Operators.threeExchange;
import static algorithms.Operators.transportAll;
import static algorithms.Operators.twoExchange;
import static java.lang.Math.E;
import static java.lang.Math.pow;
import static utils.Constants.ITERATIONS;
import static utils.Constants.random;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;
import static utils.PDPUtils.initialCost;
import static utils.PDPUtils.initialSolution;
import static utils.PDPUtils.problem;

public class SearchingAlgorithms {

    // RANDOM SEARCH

    public int[] randomSearch() {

        int[] bestSolution = initialSolution.clone();
        double bestCost = initialCost;

        int[] currentSolution;
        double currentCost;

        for (int i = 0; i < ITERATIONS; i++) {

            currentSolution = randomSolution();
            currentCost = costFunction(currentSolution, problem);

            if (feasibilityCheck(currentSolution, problem) && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
            }
        }
        return bestSolution;
    }

    // LOCAL SEARCH

    public int[] localSearch() {
        return localSearch(0.33, 0.33);
    }

    public int[] localSearch(double P1, double P2) {

        int[] bestSolution = initialSolution.clone();
        double bestCost = initialCost;

        int[] currentSolution;
        double currentCost;

        for (int i = 0; i < ITERATIONS; i++) {

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

    public int[] simulatedAnnealing() {
        return simulatedAnnealing(0.33, 0.33, 200, 0.999);
    }

    public int[] simulatedAnnealing(double P1, double P2, double T0, double a) {

        int[] incumbentSolution = initialSolution.clone();
        double incumbentCost = initialCost;

        int[] bestSolution = incumbentSolution.clone();
        double bestCost = incumbentCost;

        int[] currentSolution;
        double currentCost;

        double T = T0;
        double deltaE;
        double p;

        for (int i = 0; i < ITERATIONS; i++) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = twoExchange(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = threeExchange(incumbentSolution);
            } else {
                currentSolution = oneInsert(incumbentSolution);
            }

            currentCost = costFunction(currentSolution, problem);

            deltaE = currentCost - incumbentCost;

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

    // SIMULATED ANNEALING NEW OPERATORS

    public int[] simulatedAnnealingNewOperators() {
        return simulatedAnnealingNewOperators(100, 0.999);
    }

    public int[] simulatedAnnealingNewOperators(double T0, double a) {

        int[] incumbentSolution = initialSolution.clone();
        double incumbentCost = initialCost;

        int[] bestSolution = incumbentSolution.clone();
        double bestCost = incumbentCost;

        int[] currentSolution;
        double currentCost;

        double T = T0;
        double deltaE;
        double p;

        double P3 = Operators.percentageTransported(incumbentSolution) / 2;
        double P1 = (1 - P3) / 2;
        double P2 = (1 - P3) / 2;

        for (int i = 0; i < ITERATIONS; i++) {

            p = random.nextDouble();

            if (p < P1) {
                currentSolution = twoExchange(incumbentSolution);
            } else if (p < P1 + P2) {
                currentSolution = threeExchange(incumbentSolution);
            } else {
                currentSolution = transportAll(incumbentSolution);
            }

            currentCost = costFunction(currentSolution, problem);

            deltaE = currentCost - incumbentCost;

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
