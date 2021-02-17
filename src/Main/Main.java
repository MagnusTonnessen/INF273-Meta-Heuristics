package Main;

import Assignments.SearchingAlgorithms;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import static Utilities.PDPUtils.*;
import static Utilities.Utils.*;

public class Main {

    static String C7V3 = "Call_7_Vehicle_3.txt";
    static String C18V5 = "Call_18_Vehicle_5.txt";
    static String C35V7 = "Call_35_Vehicle_7.txt";
    static String C80V20 = "Call_80_Vehicle_20.txt";
    static String C130V40 = "Call_130_Vehicle_40.txt";

    static String blindRandomSearch = "blindRandomSearch";
    static String localSearch = "localSearch";
    static String simulatedAnnealing = "simulatedAnnealing";

    static String[] instances = new String[] {C7V3, C18V5, C35V7, C80V20, C130V40};
    static String[] searchingAlgorithms = new String[] {simulatedAnnealing};

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);

        for (String search :searchingAlgorithms) {
            Method searchingAlgorithm = SearchingAlgorithms.class.getMethod(search, int[].class, Map.class);
            for (String instance : instances) {
                runInstance(instance, searchingAlgorithm);
            }
        }
    }

    public static void runInstance(String fileName, Method searchingAlgorithm) throws Exception {

        Map<String, Object> problem = loadProblem("resources/" + fileName);

        int[] initialSolution = generateInitSolution(problem);
        double initialCost = costFunction(initialSolution, problem);
        int[] bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        printRunInfo(fileName, searchingAlgorithm, initialSolution, initialCost);

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

        printRunResults(searchingAlgorithm, totalCost, bestCost, bestSolution, initialCost, executionTime);
    }
}
