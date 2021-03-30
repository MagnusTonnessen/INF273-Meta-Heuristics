package algorithms;

import objects.Solution;

import static main.Main.initialCost;
import static main.Main.initialSolution;
import static utils.Constants.ITERATIONS;

public class AdaptiveLargeNeighbourhoodSearch implements SearchingAlgorithm {
    @Override
    public Solution search() {
        return ALNS();
    }

    public Solution ALNS() {
        Solution bestSolution = initialSolution;
        double bestCost = initialCost;

        for (int i = 0; i < ITERATIONS; i++) {

        }
        return bestSolution;
    }
}
