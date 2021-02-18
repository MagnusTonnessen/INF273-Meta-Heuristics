package Main;

import Code.SearchingAlgorithms;
import Utilities.PDFCreator;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static Utilities.PDPUtils.costFunction;
import static Utilities.PDPUtils.generateInitSolution;
import static Utilities.PDPUtils.loadProblem;
import static Utilities.Utils.getAlgorithmName;
import static Utilities.Utils.getInstanceName;
import static Utilities.Utils.rightPad;

public class Main {

    static final String C7V3 = "Call_7_Vehicle_3.txt";
    static final String C18V5 = "Call_18_Vehicle_5.txt";
    static final String C35V7 = "Call_35_Vehicle_7.txt";
    static final String C80V20 = "Call_80_Vehicle_20.txt";
    static final String C130V40 = "Call_130_Vehicle_40.txt";

    static final String RANDOM_SEARCH = "randomSearch";
    static final String LOCAL_SEARCH = "localSearch";
    static final String SIMULATED_ANNEALING = "simulatedAnnealing";

    static final String[] instances = new String[] {C7V3, C18V5, C35V7, C80V20, C130V40};
    static final String[] searchingAlgorithms = new String[] {RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING};

    static PDFCreator pdf;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        runAllInstances();
    }

    public static void runAllInstancesAndWriteToPDF() throws Exception {
        pdf = new PDFCreator("Results.pdf");
        pdf.openDocument();

        for (String instance : instances) {

            String bestAlgorithm = "";
            int[] bestSolution = new int[0];
            double bestCost = Integer.MAX_VALUE;

            pdf.newTable(getInstanceName(instance));

            for (String search : searchingAlgorithms) {
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search, int[].class, Map.class);

                Map<String, Object> searchResults = runInstance(instance, searchingAlgorithm);

                if ((double) searchResults.get("Best cost") < bestCost) {
                    bestAlgorithm = getAlgorithmName(searchingAlgorithm);
                    bestSolution = (int[]) searchResults.get("Best solution");
                    bestCost = (double) searchResults.get("Best cost");
                }
            }

            pdf.addTableAndBestSolution(bestSolution, bestAlgorithm);
        }

        pdf.closeDocument();
    }

    public static void runAllInstances() throws Exception {
        for (String instance : instances) {

            System.out.println("\n" + getInstanceName(instance));

            System.out.println(
                    rightPad("", 22) +
                    rightPad("Average objective", 20) +
                    rightPad("Best objective", 17) +
                    rightPad("Improvement (%)", 18) +
                    rightPad("Running time (s)", 20)
            );

            String bestAlgorithm = "";
            double bestCost = Integer.MAX_VALUE;

            for (String search : searchingAlgorithms) {
                Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search, int[].class, Map.class);

                Map<String, Object> searchResults = runInstance(instance, searchingAlgorithm);

                if ((double) searchResults.get("Best cost") < bestCost) {
                    bestAlgorithm = getAlgorithmName(searchingAlgorithm);
                    bestCost = (double) searchResults.get("Best cost");
                }

                DecimalFormat format = new DecimalFormat("0.00#");

                System.out.println("\r" +
                    rightPad(getAlgorithmName(searchingAlgorithm), 22) +
                    rightPad(format.format((double) searchResults.get("Average cost")), 20) +
                    rightPad(format.format((double) searchResults.get("Best cost")), 17) +
                    rightPad(format.format((double) searchResults.get("Improvement")), 18) +
                    rightPad(format.format((double) searchResults.get("Average execution time")), 19)
                );
            }

            System.out.println("\nBest solution found with " + bestAlgorithm + "\n");
        }
    }

    public static Map<String, Object> runInstance(String filePath, Method searchingAlgorithm) throws Exception {

        String algorithmName = getAlgorithmName(searchingAlgorithm);
        String instanceName = getInstanceName(filePath);
        Map<String, Object> problem = loadProblem("resources/" + filePath);

        int[] initialSolution = generateInitSolution(problem);
        double initialCost = costFunction(initialSolution, problem);
        int[] bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        //printRunInfo(instanceName, algorithmName, initialSolution, initialCost);

        for (int i = 0; i < 10; i++) {
            System.out.print("\rProgress: " + (i+1) + "/" + 10);
            long startTime = System.currentTimeMillis();
            int[] solution = (int[]) searchingAlgorithm.invoke(new SearchingAlgorithms(), initialSolution, problem);
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
        //printRunResults(algorithmName, resultsMap);

        return resultsMap;
    }
}
