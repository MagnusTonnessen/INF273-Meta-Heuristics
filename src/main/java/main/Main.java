package main;

import algorithms.SearchingAlgorithms;
import utils.JSONCreator;
import utils.PDFCreator;
import utils.Results;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static utils.Constants.C7V3;
import static utils.Constants.INSTANCES;
import static utils.Constants.LOCAL_SEARCH;
import static utils.Constants.RANDOM_SEARCH;
import static utils.Constants.REDUCE_WAIT_TIME_DESCRIPTION;
import static utils.Constants.SEARCHING_ALGORITHMS;
import static utils.Constants.SIMILAR_CALLS_DESCRIPTION;
import static utils.Constants.SIMULATED_ANNEALING;
import static utils.Constants.SIMULATED_ANNEALING_NEW_OPERATORS;
import static utils.Constants.TRANSPORT_ALL_DESCRIPTION;
import static utils.Constants.results;
import static utils.Constants.searchingAlgorithms;
import static utils.PDPUtils.callsFromID;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.initialize;
import static utils.PDPUtils.notTransportedCalls;
import static utils.PDPUtils.problem;
import static utils.Utils.getAlgorithmName;
import static utils.Utils.getInstanceName;
import static utils.Utils.printRunInfo;
import static utils.Utils.printRunResults;

public class Main {

    static PDFCreator pdf;
    static JSONCreator json;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        initialize(C7V3);
        int mostExpensiveCall = Arrays.stream(callsFromID(notTransportedCalls(problem.initialSolution))).max(Comparator.comparingInt(a -> a.costNotTransport)).get().callIndex;
        System.out.println(mostExpensiveCall);
        Arrays.stream(problem.calls).forEach(System.out::println);
        // runAllInstances();
    }

    public static void runAllInstances() throws Exception {
        runInstances(Arrays.asList(SEARCHING_ALGORITHMS));
    }

    public static void runInstances(List<String> algorithms) throws Exception {
        for (String filePath : INSTANCES) {

            initialize(filePath);

            printRunInfo();

            List<int[]> bestSolutions = new ArrayList<>(algorithms.size());

            for (String search : algorithms) {
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search);

                Results searchResults = runInstance(searchingAlgorithm, 10, false);

                bestSolutions.add(searchResults.bestSolution());

                printRunResults(getAlgorithmName(search), searchResults);
            }

            IntStream.range(0, algorithms.size()).forEach(i -> {
                System.out.println("\nBest solution found with " + getAlgorithmName(algorithms.get(i)));
                System.out.println(Arrays.toString(bestSolutions.get(i)) + "\n");
            });
        }
    }

    public static Results runInstance(Method searchingAlgorithm, int times, boolean writeToPDF) throws Exception {

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

            results.get(instance).putIfAbsent(SIMULATED_ANNEALING_NEW_OPERATORS, new HashMap<>());

            for (String search : Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING)) {
                pdf.addRow(getAlgorithmName(search),
                        (double) resultsA3.get(instance).get(search).get("Average objective"),
                        (double) resultsA3.get(instance).get(search).get("Best objective"),
                        (double) resultsA3.get(instance).get(search).get("Improvement"),
                        (double) resultsA3.get(instance).get(search).get("Average run time"));
            }

            Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(SIMULATED_ANNEALING_NEW_OPERATORS);

            Results searchResults = runInstance(searchingAlgorithm, 10, true);
            System.out.println("\r" + getAlgorithmName(SIMULATED_ANNEALING_NEW_OPERATORS));

            results.get(instance).get(SIMULATED_ANNEALING_NEW_OPERATORS).putAll(searchResults.asMap());

            bestSolutions.add(searchResults.bestSolution());

            pdf.addTable();
        }

        pdf.addBestSolutions(bestSolutions);

        pdf.addTextBlock(TRANSPORT_ALL_DESCRIPTION);
        pdf.addTextBlock(SIMILAR_CALLS_DESCRIPTION);
        pdf.addTextBlock(REDUCE_WAIT_TIME_DESCRIPTION);

        pdf.closeDocument();

        json.save(results);
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

                Results searchResults = runInstance(searchingAlgorithm, 10, true);

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

            Results searchResults = runInstance(searchingAlgorithm, 10, true);

            bestSolutions.add(searchResults.bestSolution());

            System.out.println("\n");
            pdf.addTableAndBestSolution(bestSolutions, SEARCHING_ALGORITHMS);
        }

        pdf.closeDocument();
    }
}
