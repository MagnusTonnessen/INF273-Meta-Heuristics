package algorithms;

import objects.Solution;
import operators.Operator;
import operators.Random;

import static main.Main.initialCost;
import static main.Main.initialSolution;

public class RandomSearch implements SearchingAlgorithm {
    @Override
    public Solution search(double runtime) {

        Operator random = new Random();

        Solution bestSolution = initialSolution.copy();
        double bestCost = initialCost;

        Solution currentSolution = initialSolution.copy();
        double currentCost;

        double endTime = System.currentTimeMillis() + runtime * 1000L;

        while (System.currentTimeMillis() < endTime) {
            currentSolution = random.operate(currentSolution);
            currentCost = currentSolution.cost();

            if (currentSolution.isFeasible() && currentCost < bestCost) {
                bestSolution = currentSolution.copy();
                bestCost = currentCost;
            }
        }
        /*
        for (int i = 0; i < ITERATIONS; i++) {

            currentSolution = random.operate(currentSolution);
            currentCost = currentSolution.cost();

            if (currentSolution.isFeasible() && currentCost < bestCost) {
                bestSolution = currentSolution.copy();
                bestCost = currentCost;
            }
        }
        */
        return bestSolution;
    }
}
