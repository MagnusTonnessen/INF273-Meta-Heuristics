package main;

import algorithms.SearchingAlgorithm;
import objects.Problem;
import objects.Results;
import objects.Solution;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static utils.Constants.ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH;
import static utils.Constants.C130V40;
import static utils.Constants.C18V5;
import static utils.Constants.C35V7;
import static utils.Constants.C7V3;
import static utils.Constants.C80V20;
import static utils.Constants.INSTANCES;
import static utils.Constants.INSTANCES_EXAM;
import static utils.Constants.ITERATIONS;
import static utils.Constants.LOCAL_SEARCH;
import static utils.Constants.RANDOM_SEARCH;
import static utils.Constants.RUN_TIME_C130V40;
import static utils.Constants.RUN_TIME_C18V5;
import static utils.Constants.RUN_TIME_C35V7;
import static utils.Constants.RUN_TIME_C7V3;
import static utils.Constants.RUN_TIME_C80V20;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.SEARCH_TIMES;
import static utils.Constants.SIMULATED_ANNEALING;
import static utils.Constants.relatedRemoval;
import static utils.JSONCreator.JSONToPDF;
import static utils.JSONCreator.JSONToPDFExam;
import static utils.JSONCreator.readJSONToMap;
import static utils.JSONCreator.saveToJSON;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    public static Problem problem;
    public static Solution initialSolution;
    public static double initialCost;
    public static String instanceName;

    // TODO:
    //  VISUALIZE = false
    //  ITERATION_SEARCH = false
    //  SEARCH_TIMES = 10
    //  Insert exam instances
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        System.out.println(LocalTime.now());
        long startTime = System.currentTimeMillis();

        exam();

        long endTime = System.currentTimeMillis() - startTime;
        System.out.printf("Total runtime: %d minutes %d seconds", (endTime / 1000) / 60, (endTime / 1000) % 60);
    }

    public static void runAllSearches() throws Exception {
        runSearches(SEARCHING_ALGORITHMS, INSTANCES);
    }

    public static void runSearches(SearchingAlgorithm algorithm, String instances) throws Exception {
        runSearches(Collections.singletonList(algorithm), Collections.singletonList(instances));
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

                Results searchResults = runSearch(searchingAlgorithm, getRuntime(filePath), SEARCH_TIMES);

                bestSolutions.add(searchResults.bestSolution());

                printRunResults(searchingAlgorithm.getName(), searchResults);

            }

            for (int i = 0; i < algorithms.size(); i++) {
                System.out.println("\nBest solution found with " + algorithms.get(i).getName());
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            }
        }
    }

    public static Results runSearch(SearchingAlgorithm searchingAlgorithm, double runtime, int times) {

        String algorithmName = searchingAlgorithm.getName();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < times; i++) {

            System.out.print("\r" + algorithmName + " progress: " + (i + 1) + "/" + times + "\n");

            long startTime = System.currentTimeMillis();
            Solution solution = searchingAlgorithm.search(ITERATIONS, runtime);
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

        return new Results(Arrays.stream(bestSolution.asArray()).map(i -> i + 1).toArray(), bestCost, averageCost, improvement, averageExecutionTime);
    }

    public static void exam() throws Exception {
        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>();

        for (String filePath : INSTANCES_EXAM) {

            initialize(filePath);
            printRunInfo();

            resultsMap.put(getInstanceName(filePath), new HashMap<>());
            resultsMap.get(getInstanceName(filePath)).put(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), new HashMap<>());

            Results searchResults = runSearch(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, getRuntime(filePath), SEARCH_TIMES);

            printRunResults(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), searchResults);

            resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).putAll(searchResults.asMap());
        }

        saveToJSON(resultsMap, "src/main/results/Exam.json");
        JSONToPDFExam("src/main/results/Exam.json", "src/main/results/Exam.pdf", "Exam report INF273", ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), INSTANCES_EXAM);
    }

    public static void finalAssignment() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                readJSONToMap("src/main/results/FinalAssignment.json")
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).putIfAbsent(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), new HashMap<>());

            Results searchResults = runSearch(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, getRuntime(filePath), SEARCH_TIMES);

            printRunResults(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), searchResults);

            if ((double) resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).get("Improvement") < searchResults.improvement()) {
                resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).put("Improvement", searchResults.improvement());
                resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).put("Best objective", searchResults.bestObjective());
                resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).put("Best solution", searchResults.bestSolution());
            }

            if ((double) resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).get("Average objective") < searchResults.averageObjective()) {
                resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).put("Average objective", searchResults.averageObjective());
            }
            // resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).putAll(searchResults.asMap());
        }

        saveToJSON(resultsMap, "src/main/results/FinalAssignment.json");
    }

    public static void assignment5() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                readJSONToMap("src/main/results/Assignment4.json")
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).putIfAbsent(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), new HashMap<>());

            Results searchResults = runSearch(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, getRuntime(filePath), SEARCH_TIMES);

            printRunResults(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), searchResults);

            resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).putAll(searchResults.asMap());
        }

        saveToJSON(resultsMap, "src/main/results/Assignment5.json");
    }

    public static void assignment4() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                readJSONToMap("src/main/results/test/Assignment3.json")
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).put(SIMULATED_ANNEALING.getName(), new HashMap<>());

            Results searchResults = runSearch(SIMULATED_ANNEALING, getRuntime(filePath), SEARCH_TIMES);

            resultsMap.get(getInstanceName(filePath)).get(SIMULATED_ANNEALING.getName()).putAll(searchResults.asMap());

            printRunResults(SIMULATED_ANNEALING.getName(), searchResults);
        }

        saveToJSON(resultsMap, "src/main/results/test/Assignment4.json");
    }

    public static void assignment3() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                readJSONToMap("src/main/results/test/Assignment2.json")
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            for (SearchingAlgorithm search : Arrays.asList(LOCAL_SEARCH, SIMULATED_ANNEALING)) {

                resultsMap.get(getInstanceName(filePath)).put(search.getName(), new HashMap<>());

                Results searchResults = runSearch(search, getRuntime(filePath), SEARCH_TIMES);

                resultsMap.get(getInstanceName(filePath)).get(search.getName()).putAll(searchResults.asMap());

                printRunResults(search.getName(), searchResults);
            }
        }

        saveToJSON(resultsMap, "src/main/results/test/Assignment3.json");
    }

    public static void assignment2() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>();

        for (String filePath : INSTANCES) {

            resultsMap.put(getInstanceName(filePath), new HashMap<>());

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).put(RANDOM_SEARCH.getName(), new HashMap<>());

            Results searchResults = runSearch(RANDOM_SEARCH, getRuntime(filePath), SEARCH_TIMES);

            resultsMap.get(getInstanceName(filePath)).get(RANDOM_SEARCH.getName()).putAll(searchResults.asMap());

            printRunResults(RANDOM_SEARCH.getName(), searchResults);
        }

        saveToJSON(resultsMap, "src/main/results/test/Assignment2.json");
    }

    public static void initialize(String filePath) throws Exception {
        instanceName = getInstanceName(filePath);
        problem = new Problem(filePath);
        initialSolution = new Solution();
        initialCost = initialSolution.cost();
        relatedRemoval.calculateRelations();
    }

    public static double getRuntime(String instance) {
        return switch (instance) {
            case C7V3 -> RUN_TIME_C7V3;
            case C18V5 -> RUN_TIME_C18V5;
            case C35V7 -> RUN_TIME_C35V7;
            case C80V20 -> RUN_TIME_C80V20;
            case C130V40 -> RUN_TIME_C130V40;
            default -> getRuntime(instance.replace("exam", "assignment"));
        };
    }
}
