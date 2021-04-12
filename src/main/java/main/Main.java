package main;

import algorithms.LocalSearch;
import algorithms.RandomSearch;
import algorithms.SearchingAlgorithm;
import objects.Problem;
import objects.Results;
import objects.Solution;
import operators.escapeOperators.EscapeOperator;
import operators.insertionOperators.GreedyInsertion;
import operators.removalOperators.RandomRemoval;
import operators.removalOperators.WorstRemoval;
import utils.JSONCreator;
import utils.PDFCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static utils.Constants.BRUTE_FORCE_VEHICLE_DESCRIPTION;
import static utils.Constants.BRUTE_FORCE_VEHICLE_TITLE;
import static utils.Constants.C18V5;
import static utils.Constants.C35V7;
import static utils.Constants.INSTANCES;
import static utils.Constants.LOCAL_SEARCH;
import static utils.Constants.RANDOM_SEARCH;
import static utils.Constants.REINSERT_MOST_EXPENSIVE_DESCRIPTION;
import static utils.Constants.REINSERT_MOST_EXPENSIVE_TITLE;
import static utils.Constants.RESULTS_MAP;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.SIMULATED_ANNEALING;
import static utils.Constants.SIMULATED_ANNEALING_NEW_OPERATORS;
import static utils.Constants.TRANSPORT_ALL_DESCRIPTION;
import static utils.Constants.TRANSPORT_ALL_TITLE;
import static utils.Constants.random;
import static utils.Utils.getAlgorithmName;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    public static Problem problem;
    public static Solution initialSolution;
    public static double initialCost;
    public static Solution solution;
    public static String instanceName;
    static PDFCreator pdf;
    static JSONCreator json;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        initialize(C35V7);
        LocalSearch local = new LocalSearch();
        Solution solution = local.search();
        System.out.println(solution);
        System.out.println(solution.isFeasible());
        System.out.println(solution.cost() + "\n");

        List<Integer> removedCalls = new WorstRemoval().remove(solution, random.nextInt(5)+1);
        solution.removeCalls(removedCalls);

        System.out.println("Removed calls: " + removedCalls);

        solution = new GreedyInsertion().insert(solution, removedCalls);

        System.out.println();
        System.out.println(solution);
        System.out.println(solution.isFeasible());
        System.out.println(solution.cost());
    }

    public static void runAllSearches() throws Exception {
        runSearches(SEARCHING_ALGORITHMS, Arrays.asList(INSTANCES));
    }

    public static void runSearches(List<SearchingAlgorithm> algorithms, List<String> instances) throws Exception {
        for (String filePath : instances) {

            initialize(filePath);

            printRunInfo();

            List<int[]> bestSolutions = new ArrayList<>(algorithms.size());

            for (SearchingAlgorithm searchingAlgorithm : algorithms) {

                Results searchResults = runSearch(searchingAlgorithm, 10, false);

                bestSolutions.add(searchResults.bestSolution());

                printRunResults(searchingAlgorithm.getClass().getSimpleName(), searchResults);
            }

            IntStream.range(0, algorithms.size()).forEach(i -> {
                System.out.println("\nBest solution found with " + algorithms.get(i).getName());
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Results runSearch(SearchingAlgorithm searchingAlgorithm, int times, boolean writeToPDF) {

        String algorithmName = searchingAlgorithm.getName();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < times; i++) {

            System.out.print("\r" + algorithmName + " progress: " + (i + 1) + "/" + times);

            long startTime = System.currentTimeMillis();
            Solution solution = searchingAlgorithm.search();
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

        for (String instance : INSTANCES) {

            System.out.println("\n" + getInstanceName(instance) + "\n");

            initialize(instance);
            printRunInfo();

            pdf.newTable(getInstanceName(instance));

            RESULTS_MAP.get(instance).putIfAbsent(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), new HashMap<>());

            for (SearchingAlgorithm search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                pdf.addRow(getAlgorithmName(search.getName()),
                        (double) resultsA3.get(instance).get(search.getName()).get("Average objective"),
                        (double) resultsA3.get(instance).get(search.getName()).get("Best objective"),
                        (double) resultsA3.get(instance).get(search.getName()).get("Improvement"),
                        (double) resultsA3.get(instance).get(search.getName()).get("Average run time"));
            }


            Results searchResults = runSearch(SIMULATED_ANNEALING_NEW_OPERATORS, 10, true);

            printRunResults(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), searchResults);

            RESULTS_MAP.get(instance).get(SIMULATED_ANNEALING_NEW_OPERATORS.getName()).putAll(searchResults.asMap());

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

                Results searchResults = runSearch(search, 10, true);

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

            Results searchResults = runSearch(RANDOM_SEARCH, 10, true);

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
        solution = new Solution();
    }
}
