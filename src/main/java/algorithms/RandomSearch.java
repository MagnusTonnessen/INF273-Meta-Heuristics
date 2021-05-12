package algorithms;

import main.Main;
import objects.Solution;
import utils.Constants;

import static main.Main.initialCost;
import static utils.Constants.ITERATION_SEARCH;
import static utils.Constants.randomOperator;

public class RandomSearch implements SearchingAlgorithm {
    @Override
    public Solution search(int iterations, double runtime) {

        Solution bestSolution = Main.initialSolution.copy();
        double bestCost = initialCost;

        Solution currentSolution = Main.initialSolution.copy();
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {
            currentSolution = randomOperator.operate(currentSolution, Constants.random.nextInt(4) + 1);
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
