package main;

import algorithms.SearchingAlgorithms;
import objects.Results;
import utils.JSONCreator;
import utils.PDFCreator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static utils.Constants.BRUTE_FORCE_VEHICLE_DESCRIPTION;
import static utils.Constants.C130V40;
import static utils.Constants.C18V5;
import static utils.Constants.C35V7;
import static utils.Constants.C7V3;
import static utils.Constants.C80V20;
import static utils.Constants.FILL_ALL_VEHICLES_DESCRIPTION;
import static utils.Constants.INSTANCES;
import static utils.Constants.LOCAL_SEARCH;
import static utils.Constants.RANDOM_SEARCH;
import static utils.Constants.REINSERT_FROM_MOST_EXPENSIVE_VEHICLE;
import static utils.Constants.RESULTS_MAP;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.SIMULATED_ANNEALING;
import static utils.Constants.SIMULATED_ANNEALING_NEW_OPERATORS;
import static utils.Constants.TRANSPORT_ALL_DESCRIPTION;
import static utils.Constants.searchingAlgorithms;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.initialize;
import static utils.PDPUtils.problem;
import static utils.Utils.getAlgorithmName;
import static utils.Utils.getEmptyVehicles;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    static PDFCreator pdf;
    static JSONCreator json;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);

        runSearches(Arrays.asList(SIMULATED_ANNEALING_NEW_OPERATORS), Arrays.asList(INSTANCES));
    }

    public static void runAllSearches() throws Exception {
        runSearches(Arrays.asList(SEARCHING_ALGORITHMS), Arrays.asList(C7V3, C18V5, C35V7, C80V20, C130V40));
    }

    public static void runSearches(List<String> algorithms, List<String> instances) throws Exception {
        for (String filePath : instances) {

            initialize(filePath);

            printRunInfo();

            List<int[]> bestSolutions = new ArrayList<>(algorithms.size());

            for (String search : algorithms) {
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search);

                Results searchResults = runSearch(searchingAlgorithm, 10, false);

                bestSolutions.add(searchResults.bestSolution());

                printRunResults(getAlgorithmName(search), searchResults);
            }

            IntStream.range(0, algorithms.size()).forEach(i -> {
                System.out.println("\nBest solution found with " + getAlgorithmName(algorithms.get(i)));
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Results runSearch(Method searchingAlgorithm, int times, boolean writeToPDF) throws Exception {

        String algorithmName = getAlgorithmName(searchingAlgorithm);

        int[] bestSolution = problem.initialSolution;
        double bestCost = problem.initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < times; i++) {
            System.out.print("\r" + getAlgorithmName(searchingAlgorithm) + " progress: " + (i + 1) + "/" + times);
            long startTime = System.currentTimeMillis();
            int[] solution = (int[]) searchingAlgorithm.invoke(searchingAlgorithms);
            executionTime += System.currentTimeMillis() - startTime;
            double cost = costFunction(solution);
            totalCost += cost;
            if (cost < bestCost) {
                bestSolution = solution;
                bestCost = cost;
            }
        }

        double averageCost = totalCost / times;
        double improvement = 100.0 * (problem.initialCost - bestCost) / problem.initialCost;
        double averageExecutionTime = (executionTime / times) / 1000;

        if (writeToPDF) {
            pdf.addRow(algorithmName, averageCost, bestCost, improvement, averageExecutionTime);
        }

        return new Results(bestSolution, bestCost, averageCost, improvement, averageExecutionTime);
    }

    public static void assignment4() throws Exception {
        json = new JSONCreator("src/main/results/Assignment4.json");
        pdf = new PDFCreator("src/main/results/Assignment4.pdf");
        pdf.openDocument();

        List<int[]> bestSolutions = new ArrayList<>(5);

        Map<String, Map<String, Map<String, Object>>> resultsA3 = new JSONCreator("src/main/results/Assignment3.json").read();

        for (String instance : INSTANCES) {

            System.out.println("\n" + getInstanceName(instance) + "\n");

            initialize(instance);

            pdf.newTable(getInstanceName(instance));

            RESULTS_MAP.get(instance).putIfAbsent(SIMULATED_ANNEALING_NEW_OPERATORS, new HashMap<>());

            for (String search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                pdf.addRow(getAlgorithmName(search),
                        (double) resultsA3.get(instance).get(search).get("Average objective"),
                        (double) resultsA3.get(instance).get(search).get("Best objective"),
                        (double) resultsA3.get(instance).get(search).get("Improvement"),
                        (double) resultsA3.get(instance).get(search).get("Average run time"));
            }

            Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(SIMULATED_ANNEALING_NEW_OPERATORS);

            Results searchResults = runSearch(searchingAlgorithm, 10, true);
            System.out.println("\r" + getAlgorithmName(SIMULATED_ANNEALING_NEW_OPERATORS));

            RESULTS_MAP.get(instance).get(SIMULATED_ANNEALING_NEW_OPERATORS).putAll(searchResults.asMap());

            bestSolutions.add(searchResults.bestSolution());

            pdf.addTable();
        }

        pdf.addBestSolutions(bestSolutions);

        pdf.addTextBlock(TRANSPORT_ALL_DESCRIPTION);
        pdf.addTextBlock(REINSERT_FROM_MOST_EXPENSIVE_VEHICLE);
        pdf.addTextBlock(BRUTE_FORCE_VEHICLE_DESCRIPTION);
        pdf.addTextBlock(FILL_ALL_VEHICLES_DESCRIPTION);

        pdf.closeDocument();

        json.save(RESULTS_MAP);
    }

    public static void assignment3() throws Exception {
        json = new JSONCreator("src/main/results/Assignment3.json");
        pdf = new PDFCreator("src/main/results/Assignment3.pdf");
        pdf.openDocument();

        for (String filePath : INSTANCES) {

            System.out.println(getInstanceName(filePath));

            initialize(filePath);

            List<int[]> bestSolutions = new ArrayList<>(3);

            pdf.newTable(getInstanceName(filePath));

            for (String search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                System.out.println("\n" + getAlgorithmName(search));
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search);

                Results searchResults = runSearch(searchingAlgorithm, 10, true);

                bestSolutions.add(searchResults.bestSolution());
            }

            System.out.println("\n");
            pdf.addTableAndBestSolution(bestSolutions, SEARCHING_ALGORITHMS);
        }

        pdf.closeDocument();
    }

    public static void assignment2() throws Exception {
        json = new JSONCreator("src/main/results/Assignment2.json");
        pdf = new PDFCreator("src/main/results/Assignment2.pdf");
        pdf.openDocument();

        for (String filePath : INSTANCES) {

            System.out.println(getInstanceName(filePath));

            initialize(filePath);

            List<int[]> bestSolutions = new ArrayList<>(SEARCHING_ALGORITHMS.length);

            pdf.newTable(getInstanceName(filePath));

            System.out.println("\n" + getAlgorithmName(RANDOM_SEARCH));
            Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(RANDOM_SEARCH);

            Results searchResults = runSearch(searchingAlgorithm, 10, true);

            bestSolutions.add(searchResults.bestSolution());

            System.out.println("\n");
            pdf.addTableAndBestSolution(bestSolutions, SEARCHING_ALGORITHMS);
        }

        pdf.closeDocument();
    }
}
