package main;

import algorithms.SearchingAlgorithm;
import objects.Problem;
import objects.Results;
import objects.Solution;
import utils.JSONCreator;
import utils.PDFCreator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.Constants.ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH;
import static utils.Constants.C130V40;
import static utils.Constants.C18V5;
import static utils.Constants.C35V7;
import static utils.Constants.C7V3;
import static utils.Constants.C80V20;
import static utils.Constants.INSTANCES;
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
import static utils.Constants.SIMULATED_ANNEALING_NEW_OPERATORS;
import static utils.Constants.relatedRemoval;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    public static Problem problem;
    public static Solution initialSolution;
    public static double initialCost;
    public static String instanceName;

    // TODO:
    //  Implement related removal
    //  Swap related calls two and three exchange
    //  Fix initial temperature calculation
    // Find longest org -> org and longest dest -> dest
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        System.out.println(LocalTime.now());
        initialize(C7V3);
        /*
        long startTime = System.currentTimeMillis();
        runSearches(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, INSTANCES);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.printf("Total runtime: %d minutes %d seconds", (endTime/1000)/60, (endTime/1000)%60);
        */
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

            IntStream.range(0, algorithms.size()).forEach(i -> {
                System.out.println("\nBest solution found with " + algorithms.get(i).getName());
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Results runSearch(SearchingAlgorithm searchingAlgorithm, double runtime, int times) {

        String algorithmName = searchingAlgorithm.getName();

        Solution bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        for (int i = 0; i < times; i++) {

            System.out.print("\r" + algorithmName + " progress: " + (i + 1) + "/" + times);

            long startTime = System.currentTimeMillis();
            Solution solution = searchingAlgorithm.search(initialSolution, ITERATIONS, runtime);
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

    @SuppressWarnings("unchecked")
    public static void JSONToPDF(String jsonPath, String pdfPath, String algorithm) throws Exception {
        PDFCreator pdf = new PDFCreator(pdfPath);
        pdf.openDocument();

        Map<String, Map<String, Map<String, Object>>> jsonAsMap = new JSONCreator(jsonPath).read();

        for (String filePath : INSTANCES) {

            pdf.newTable(getInstanceName(filePath));

            for (SearchingAlgorithm search : SEARCHING_ALGORITHMS) {
                try {
                    pdf.addRow(search.getName(),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Average objective"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Best objective"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Improvement"),
                            (double) jsonAsMap.get(getInstanceName(filePath)).get(search.getName()).get("Average run time")
                    );
                } catch (Exception ignored) {
                }
            }

            pdf.addTable();
        }

        pdf.addTitle("Best solutions found with " + algorithm);

        List<int[]> bestSolutions = jsonAsMap
                .values()
                .stream()
                .map(instance -> ((List<Integer>) instance.get(algorithm).get("Best solution")).stream().mapToInt(i -> i).toArray())
                .sorted(Comparator.comparingInt(solution -> solution.length))
                .collect(Collectors.toList());

        pdf.addBestSolutions(bestSolutions);

        pdf.closeDocument();
    }

    public static void assignment5() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                new JSONCreator("src/main/results/Assignment4.json").read()
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).putIfAbsent(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), new HashMap<>());

            Results searchResults = runSearch(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH, getRuntime(filePath), SEARCH_TIMES);

            printRunResults(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName(), searchResults);

            resultsMap.get(getInstanceName(filePath)).get(ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH.getName()).putAll(searchResults.asMap());
        }

        new JSONCreator("src/main/results/Assignment5.json").save(resultsMap);
    }

    public static void assignment4() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                new JSONCreator("src/main/results/test/Assignment3test.json").read()
        );

        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            resultsMap.get(getInstanceName(filePath)).put(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), new HashMap<>());

            Results searchResults = runSearch(SIMULATED_ANNEALING_NEW_OPERATORS, getRuntime(filePath), SEARCH_TIMES);

            resultsMap.get(getInstanceName(filePath)).get(SIMULATED_ANNEALING_NEW_OPERATORS.getName()).putAll(searchResults.asMap());

            printRunResults(SIMULATED_ANNEALING_NEW_OPERATORS.getName(), searchResults);
        }

        new JSONCreator("src/main/results/test/Assignment4test.json").save(resultsMap);
    }

    public static void assignment3() throws Exception {

        Map<String, Map<String, Map<String, Object>>> resultsMap = new HashMap<>(
                new JSONCreator("src/main/results/test/Assignment2test.json").read()
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

        new JSONCreator("src/main/results/test/Assignment3test.json").save(resultsMap);
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

        new JSONCreator("src/main/results/test/Assignment2test.json").save(resultsMap);
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
