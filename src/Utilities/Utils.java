package Utilities;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class Utils {

    public static void printRunInfo(String instanceName, String algorithmName, int[] initialSolution, double initialCost) {
        System.out.println("\n--- Running " + algorithmName + " on " + instanceName + " ---\n");
        System.out.println("Initial solution: " + Arrays.toString(initialSolution));
        System.out.printf("Initial cost: %.2f\n\n", initialCost);
    }

    public static void printRunResults(String algorithmName, Map<String, Object> results) {
        System.out.printf("\n\nAverage cost: %.2f\n" , (double) results.get("Average cost"));
        System.out.printf("Best cost: %.2f\n", (double) results.get("Best cost"));
        System.out.println("Best solution: " + Arrays.toString((int[]) results.get("Best solution")));
        System.out.printf("Improvement: %.2f %%\n", (double) results.get("Improvement"));
        System.out.printf("Average execution time: %.3f seconds\n", (double) results.get("Average execution time"));
        System.out.println("\n--- End of " + algorithmName + " ---");
    }

    public static String rightPad(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    public static String getAlgorithmName(Method algorithm) {
        return Arrays
                .stream(algorithm.getName().split("(?=[A-Z])"))
                .map(w -> w.substring(0,1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getInstanceName(String instance) {
        return instance.substring(0, instance.length() - 4).replace("_", " ");
    }
}