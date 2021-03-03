package main;

import algorithms.SearchingAlgorithms;
import utils.PDFCreator;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static utils.Constants.FILE_PATHS;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.initialize;
import static utils.Constants.searchingAlgorithms;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.generateInitSolution;
import static utils.PDPUtils.loadProblem;
import static utils.Utils.getAlgorithmName;
import static utils.Utils.getInstanceName;
import static utils.Utils.rightPad;

public class Main {

    static PDFCreator pdf;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        runAllInstancesAndWriteToPDF();
    }

    public static void runAllInstancesAndWriteToPDF() throws Exception {
        pdf = new PDFCreator("Assignment3.pdf");
        pdf.openDocument();

        for (String filePath : FILE_PATHS) {

            System.out.println(getInstanceName(filePath));

            initialize(filePath);

            List<int[]> bestSolutions = new ArrayList<>(SEARCHING_ALGORITHMS.length);

            pdf.newTable(getInstanceName(filePath));

            for (String search : SEARCHING_ALGORITHMS) {
                System.out.println("\n" + getAlgorithmName(search));
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search);

                Map<String, Object> searchResults = runInstance(filePath, searchingAlgorithm);

                bestSolutions.add((int[]) searchResults.get("Best solution"));
            }

            System.out.println("\n");
            pdf.addTableAndBestSolution(bestSolutions, SEARCHING_ALGORITHMS);
        }

        pdf.closeDocument();
    }

    public static void runAllInstances() throws Exception {
        for (String filePath : FILE_PATHS) {

            System.out.println("\n" + getInstanceName(filePath));

            System.out.println(
                    rightPad("", 22) +
                    rightPad("Average objective", 20) +
                    rightPad("Best objective", 17) +
                    rightPad("Improvement (%)", 18) +
                    rightPad("Running time (s)", 20)
            );

            List<int[]> bestSolutions = new ArrayList<>(SEARCHING_ALGORITHMS.length);

            for (String search : SEARCHING_ALGORITHMS) {
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search, int[].class, Map.class);

                Map<String, Object> searchResults = runInstance(filePath, searchingAlgorithm);

                bestSolutions.add((int[]) searchResults.get("Best solution"));

                DecimalFormat format = new DecimalFormat("0.00#");

                System.out.println("\r" +
                    rightPad(getAlgorithmName(searchingAlgorithm), 22) +
                    rightPad(format.format((double) searchResults.get("Average cost")), 20) +
                    rightPad(format.format((double) searchResults.get("Best cost")), 17) +
                    rightPad(format.format((double) searchResults.get("Improvement")), 18) +
                    rightPad(format.format((double) searchResults.get("Average execution time")), 19)
                );
            }

            IntStream.range(0, SEARCHING_ALGORITHMS.length).forEach(i -> {
                System.out.println("\nBest solution found with " + getAlgorithmName(SEARCHING_ALGORITHMS[i]));
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Map<String, Object> runInstance(String filePath, Method searchingAlgorithm) throws Exception {

        String algorithmName = getAlgorithmName(searchingAlgorithm);
        String instanceName = getInstanceName(filePath);
        Map<String, Object> problem = loadProblem(filePath);

        int[] initialSolution = generateInitSolution(problem);
        double initialCost = costFunction(initialSolution, problem);
        int[] bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < 10; i++) {
            System.out.print("\rProgress: " + (i+1) + "/" + 10);
            long startTime = System.currentTimeMillis();
            int[] solution = (int[]) searchingAlgorithm.invoke(searchingAlgorithms);
            executionTime += System.currentTimeMillis() - startTime;
            double cost = costFunction(solution, problem);
            totalCost += cost;
            if (cost < bestCost) {
                bestSolution = solution;
                bestCost = cost;
            }
        }

        double averageCost = totalCost / 10;
        double improvement = 100.0 * (initialCost - bestCost) / initialCost;
        double averageExecutionTime = (executionTime / 10) / 1000;

        Map<String, Object> resultsMap = new HashMap<>();
        resultsMap.put("Best cost", bestCost);
        resultsMap.put("Best solution", bestSolution);
        resultsMap.put("Average cost", averageCost);
        resultsMap.put("Improvement", improvement);
        resultsMap.put("Average execution time", averageExecutionTime);

        if (pdf != null) {
            pdf.addRow(algorithmName, averageCost, bestCost, improvement, averageExecutionTime);
        }

        return resultsMap;
    }
}
