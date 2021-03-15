package utils;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;
import static utils.PDPUtils.initialCost;
import static utils.PDPUtils.instanceName;

public class Utils {

    public static final int pad = 20;

    public static void printRunInfo() {
        System.out.println("\n--- " + instanceName + " ---\n");
        System.out.printf("Initial cost: %.2f\n\n", initialCost);

        System.out.println(
                rightPad("", pad + 25) +
                        rightPad("Average objective", pad) +
                        rightPad("Best objective", pad) +
                        rightPad("Improvement (%)", pad) +
                        rightPad("Running time (seconds)", pad)
        );
    }

    public static void printRunResults(String algorithmName, Results results) {
        DecimalFormat format = new DecimalFormat("0.00#");

        System.out.println("\r" +
                rightPad(algorithmName, pad + 25) +
                rightPad(format.format(results.averageObjective()), pad) +
                rightPad(format.format(results.bestObjective()), pad) +
                rightPad(format.format(results.improvement()), pad) +
                rightPad(format.format(results.averageRunTime()), pad)
        );
    }

    public static String rightPad(String text, int length) {
        return String.format("%1$-" + length + "s", text);
    }

    public static String getAlgorithmName(Method algorithm) {
        if (algorithm.getName().contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.getName().contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.getName().split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getAlgorithmName(String algorithm) {
        if (algorithm.contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getInstanceName(String filePath) {
        return filePath
                .replace("src/main/resources/", "")
                .replace("_", " ")
                .replace(".txt", "");
    }
}
