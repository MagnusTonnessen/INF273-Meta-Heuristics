package utils;

import algorithms.SearchingAlgorithms;

import java.util.Map;

import static utils.PDPUtils.generateInitSolution;
import static utils.PDPUtils.loadProblem;

public class Constants {
    public static final String C7V3 = "src/main/resources/Call_7_Vehicle_3.txt";
    public static final String C18V5 = "src/main/resources/Call_18_Vehicle_5.txt";
    public static final String C35V7 = "src/main/resources/Call_35_Vehicle_7.txt";
    public static final String C80V20 = "src/main/resources/Call_80_Vehicle_20.txt";
    public static final String C130V40 = "src/main/resources/Call_130_Vehicle_40.txt";

    public static final String RANDOM_SEARCH = "randomSearch";
    public static final String LOCAL_SEARCH = "localSearch";
    public static final String SIMULATED_ANNEALING = "simulatedAnnealing";

    public static final String[] FILE_PATHS = new String[] {C7V3, C18V5, C35V7, C80V20, C130V40};
    public static final String[] SEARCHING_ALGORITHMS = new String[] {RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING};

    public static final int ITERATIONS = 10000;

    public static final SearchingAlgorithms searchingAlgorithms = new SearchingAlgorithms();

    public static Map<String, Object> problem;
    public static int[] initialSolution;

    public static void initialize(String filePath) {
        problem = loadProblem(filePath);
        initialSolution = generateInitSolution(problem);
    }
}
