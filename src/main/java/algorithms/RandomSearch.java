package algorithms;

import main.Main;
import objects.Solution;
import operators.oldOperators.Operator;
import operators.oldOperators.Random;
import utils.Constants;

import static main.Main.initialCost;
import static utils.Constants.ITERATION_SEARCH;

public class RandomSearch implements SearchingAlgorithm {
    @Override
    public Solution search(Solution initialSolution, int iterations, double runtime) {

        Operator random = new Random();

        Solution bestSolution = Main.initialSolution.copy();
        double bestCost = initialCost;

        Solution currentSolution = Main.initialSolution.copy();
        double currentCost;

        int iteration = 0;
        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while ((ITERATION_SEARCH && iteration < iterations) || (!ITERATION_SEARCH && System.currentTimeMillis() < endTime)) {
            currentSolution = random.operate(currentSolution, Constants.random.nextInt(4) + 1);
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
