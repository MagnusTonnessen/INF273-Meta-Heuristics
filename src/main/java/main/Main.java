package main;

import algorithms.SearchingAlgorithm;
import objects.Problem;
import objects.Results;
import objects.Solution;
import utils.JSONCreator;
import utils.PDFCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static utils.Constants.ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH;
import static utils.Constants.BRUTE_FORCE_VEHICLE_DESCRIPTION;
import static utils.Constants.BRUTE_FORCE_VEHICLE_TITLE;
import static utils.Constants.C130V40;
import static utils.Constants.C18V5;
import static utils.Constants.C35V7;
import static utils.Constants.C7V3;
import static utils.Constants.C80V20;
import static utils.Constants.INSTANCES;
import static utils.Constants.LOCAL_SEARCH;
import static utils.Constants.RANDOM_SEARCH;
import static utils.Constants.REINSERT_MOST_EXPENSIVE_DESCRIPTION;
import static utils.Constants.REINSERT_MOST_EXPENSIVE_TITLE;
import static utils.Constants.RESULTS_MAP;
import static utils.Constants.RUN_TIME_C130V40;
import static utils.Constants.RUN_TIME_C18V5;
import static utils.Constants.RUN_TIME_C35V7;
import static utils.Constants.RUN_TIME_C7V3;
import static utils.Constants.RUN_TIME_C80V20;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.SEARCH_TIMES;
import static utils.Constants.SIMULATED_ANNEALING;
import static utils.Constants.SIMULATED_ANNEALING_NEW_OPERATORS;
import static utils.Constants.TRANSPORT_ALL_DESCRIPTION;
import static utils.Constants.TRANSPORT_ALL_TITLE;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    public static Problem problem;
    public static Solution initialSolution;
    public static double initialCost;
    public static String instanceName;
    static PDFCreator pdf;
    static JSONCreator json;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        runSearches(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, INSTANCES);
    }

    public static void runAllSearches() throws Exception {
        runSearches(SEARCHING_ALGORITHMS, INSTANCES);
    }

    public static void runSearches(SearchingAlgorithm algorithm, List<String> instances) throws Exception {
        runSearches(Collections.singletonList(algorithm), instances);
    }

    public static void runSearches(List<SearchingAlgorithm> algorithms, List<String> instances) throws Exception {
        for (String filePath : instances) {

            initialize(filePath);

            printRunInfo();

            List<int[]> bestSolutions = new ArrayList<>(algorithms.size());

            for (SearchingAlgorithm searchingAlgorithm : algorithms) {

                Results searchResults = runSearch(searchingAlgorithm, getRuntime(filePath), SEARCH_TIMES, false);

                bestSolutions.add(searchResults.bestSolution());

                printRunResults(searchingAlgorithm.getName(), searchResults);
            }

            IntStream.range(0, algorithms.size()).forEach(i -> {
                System.out.println("\nBest solution found with " + algorithms.get(i).getName());
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Results runSearch(SearchingAlgorithm searchingAlgorithm, double runtime, int times, boolean writeToPDF) {

        String algorithmName = searchingAlgorithm.getName();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < times; i++) {

            System.out.print("\r" + algorithmName + " progress: " + (i + 1) + "/" + times);

            long startTime = System.currentTimeMillis();
            Solution solution = searchingAlgorithm.search(runtime);
            executionTime += System.currentTimeMillis() - startTime;

            double cost = solution.cost();
            totalCost += cost;

            if (cost < bestCost) {
                bestSolution = solution;
                bestCost = cost;
            }
        }

        double averageCost = totalCost / times;
        double improvement = 100.0 * (initialCost - bestCost) / initialCost;
        double averageExecutionTime = (executionTime / times) / 1000;

        if (writeToPDF) {
            pdf.addRow(algorithmName, averageCost, bestCost, improvement, averageExecutionTime);
        }

        return new Results(Arrays.stream(bestSolution.asArray()).map(i -> i + 1).toArray(), bestCost, averageCost, improvement, averageExecutionTime);
    }

    public static void assignment4() throws Exception {
        json = new JSONCreator("src/main/results/Assignment4.json");
        pdf = new PDFCreator("src/main/results/Assignment4.pdf");
        pdf.openDocument();

        List<int[]> bestSolutions = new ArrayList<>(5);

        Map<String, Map<String, Map<String, Object>>> resultsA3 = new JSONCreator("src/main/results/Assignment3.json").read();

        for (String filePath : INSTANCES) {

            System.out.println("\n" + getInstanceName(filePath) + "\n");

            initialize(filePath);
            printRunInfo();

            pdf.newTable(getInstanceName(filePath));

            RESULTS_MAP.get(filePath).putIfAbsent(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), new HashMap<>());

            for (SearchingAlgorithm search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                pdf.addRow(search.getName(),
                        (double) resultsA3.get(filePath).get(search.getName()).get("Average objective"),
                        (double) resultsA3.get(filePath).get(search.getName()).get("Best objective"),
                        (double) resultsA3.get(filePath).get(search.getName()).get("Improvement"),
                        (double) resultsA3.get(filePath).get(search.getName()).get("Average run time"));
            }


            Results searchResults = runSearch(SIMULATED_ANNEALING_NEW_OPERATORS, getRuntime(filePath), SEARCH_TIMES, true);

            printRunResults(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), searchResults);

            RESULTS_MAP.get(filePath).get(SIMULATED_ANNEALING_NEW_OPERATORS.getName()).putAll(searchResults.asMap());

            bestSolutions.add(searchResults.bestSolution());

            pdf.addTable();
        }

        pdf.addTitle("Best solutions found with " + SIMULATED_ANNEALING_NEW_OPERATORS.getName());

        pdf.addBestSolutions(bestSolutions);

        pdf.addTitle(TRANSPORT_ALL_TITLE);
        pdf.addTextBlock(TRANSPORT_ALL_DESCRIPTION);

        pdf.addTitle(REINSERT_MOST_EXPENSIVE_TITLE);
        pdf.addTextBlock(REINSERT_MOST_EXPENSIVE_DESCRIPTION);

        pdf.addTitle(BRUTE_FORCE_VEHICLE_TITLE);
        pdf.addTextBlock(BRUTE_FORCE_VEHICLE_DESCRIPTION);

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

            for (SearchingAlgorithm search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                System.out.println("\n" + search.getName());

                Results searchResults = runSearch(search, getRuntime(filePath), SEARCH_TIMES, true);

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

            List<int[]> bestSolutions = new ArrayList<>(1);

            pdf.newTable(getInstanceName(filePath));

            System.out.println("\n" + RANDOM_SEARCH.getName());

            Results searchResults = runSearch(RANDOM_SEARCH, getRuntime(filePath), SEARCH_TIMES, true);

            bestSolutions.add(searchResults.bestSolution());

            System.out.println("\n");
            pdf.addTableAndBestSolution(bestSolutions, SEARCHING_ALGORITHMS);
        }

        pdf.closeDocument();
    }

    public static void initialize(String filePath) throws Exception {
        instanceName = getInstanceName(filePath);
        problem = new Problem(filePath);
        initialSolution = new Solution();
        initialCost = initialSolution.cost();
    }

    public static double getRuntime(String instance) {
        return switch (instance) {
            case C7V3 -> RUN_TIME_C7V3;
            case C18V5 -> RUN_TIME_C18V5;
            case C35V7 -> RUN_TIME_C35V7;
            case C80V20 -> RUN_TIME_C80V20;
            case C130V40 -> RUN_TIME_C130V40;
            default -> throw new IllegalStateException("Unexpected value: " + instance);
        };
    }
}
