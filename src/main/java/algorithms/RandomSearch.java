package algorithms;

import objects.Solution;
import operators.oldOperators.Operator;
import operators.oldOperators.Random;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;
import static utils.Constants.ITERATION_SEARCH;

public class RandomSearch implements SearchingAlgorithm {
    @Override
    public Solution search(double runtime) {

        Operator random = new Random();

        Solution bestSolution = initialSolution.copy();
        double bestCost = initialCost;

        Solution currentSolution = initialSolution.copy();
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < ITERATIONS) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {
            currentSolution = random.operate(currentSolution);
            currentCost = currentSolution.cost();

            if (currentSolution.isFeasible() && currentCost < bestCost) {
                bestSolution = currentSolution.copy();
                bestCost = currentCost;
            }
            iteration++;
        }
        return bestSolution;
    }
}
