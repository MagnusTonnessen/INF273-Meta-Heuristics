package utils;

import algorithms.SearchingAlgorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.toMap;

public class Constants {
    public static final String C7V3 = "src/main/resources/Call_7_Vehicle_3.txt";
    public static final String C18V5 = "src/main/resources/Call_18_Vehicle_5.txt";
    public static final String C35V7 = "src/main/resources/Call_35_Vehicle_7.txt";
    public static final String C80V20 = "src/main/resources/Call_80_Vehicle_20.txt";
    public static final String C130V40 = "src/main/resources/Call_130_Vehicle_40.txt";

    public static final String RANDOM_SEARCH = "randomSearch";
    public static final String LOCAL_SEARCH = "localSearch";
    public static final String SIMULATED_ANNEALING = "simulatedAnnealing";
    public static final String SIMULATED_ANNEALING_NEW_OPERATORS = "simulatedAnnealingNewOperators";

    public static final String[] INSTANCES = new String[]{C7V3, C18V5, C35V7, C80V20, C130V40};
    public static final String[] SEARCHING_ALGORITHMS = new String[]{RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING, SIMULATED_ANNEALING_NEW_OPERATORS};

    public static final int ITERATIONS = 1000;

    public static final SearchingAlgorithms searchingAlgorithms = new SearchingAlgorithms();
    public static final Random random = new Random();

    public static final String TRANSPORT_ALL_DESCRIPTION = "Description transport all";
    public static final String SIMILAR_CALLS_DESCRIPTION = "Description similar calls";
    public static final String REDUCE_WAIT_TIME_DESCRIPTION = "Description reduce wait time";

    public static final Map<String, Map<String, Map<String, Object>>> results = Arrays.stream(INSTANCES).collect(toMap(i -> i, i -> new HashMap<>()));
}
