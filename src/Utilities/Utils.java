package Utilities;

import java.lang.reflect.Method;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class Utils {

    public static void printRunInfo(String fileName, Method searchingAlgorithm, int[] initialSolution, double initialCost) {
        String algoName = Arrays.stream(searchingAlgorithm.getName().split("(?=[A-Z])")).map(w -> w.substring(0,1).toUpperCase() + w.substring(1)).collect(joining(" "));
        String datasetName = fileName.substring(0, fileName.length() - 4).replace("_", " ");
        System.out.println("\n--- Running " + algoName + " on " + datasetName + " ---\n");
        System.out.println("Initial solution: " + Arrays.toString(initialSolution));
        System.out.printf("Initial cost: %.2f\n\n", initialCost);
    }

    public static void printRunResults(Method searchingAlgorithm, double totalCost, double bestCost, int[] bestSolution, double initialCost, double executionTime) {
        System.out.printf("\n\nAverage cost: %.2f\n" , totalCost / 10);
        System.out.printf("Best cost: %.2f\n", bestCost);
        System.out.println("Best solution: " + Arrays.toString(bestSolution));
        System.out.printf("Improvement: %.2f %%\n", (100.0 * (initialCost - bestCost) / initialCost));
        System.out.printf("Average execution time: %.3f seconds\n", (executionTime / 10) / 1000);
        String algoName = Arrays.stream(searchingAlgorithm.getName().split("(?=[A-Z])")).map(w -> w.substring(0,1).toUpperCase() + w.substring(1)).collect(joining(" "));
        System.out.println("\n--- End of " + algoName + " ---");
    }


}
