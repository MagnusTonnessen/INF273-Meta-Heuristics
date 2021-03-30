package algorithms;

import objects.Solution;
import operators.Operator;
import operators.Random;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;

public class RandomSearch implements SearchingAlgorithm {
    @Override
    public Solution search() {

        Operator random = new Random();

        Solution bestSolution = initialSolution.copy();
        double bestCost = initialCost;

        Solution currentSolution = initialSolution.copy();
        double currentCost;

        for (int i = 0; i < ITERATIONS; i++) {

            currentSolution = random.operate(currentSolution);
            currentCost = currentSolution.cost();

            if (currentSolution.isFeasible() && currentCost < bestCost) {
                bestSolution = currentSolution.copy();
                bestCost = currentCost;
            }
        }
        return bestSolution;
    }
}
