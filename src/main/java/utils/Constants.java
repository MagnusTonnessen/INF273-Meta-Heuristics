package utils;

import algorithms.AdaptiveLargeNeighbourhoodSearch;
import algorithms.LocalSearch;
import algorithms.RandomSearch;
import algorithms.SearchingAlgorithm;
import algorithms.SimulatedAnnealing;
import operators.insertionHeuristics.GreedyInsertion;
import operators.insertionHeuristics.RegretKInsertion;
import operators.operators.OneInsert;
import operators.operators.Operator;
import operators.operators.RandomOperator;
import operators.operators.RandomRemovalGreedyInsertion;
import operators.operators.RandomRemovalRegretKInsertion;
import operators.operators.RelatedRemovalGreedyInsertion;
import operators.operators.RelatedRemovalRegretKInsertion;
import operators.operators.ThreeExchange;
import operators.operators.TwoExchange;
import operators.operators.WorstRemovalGreedyInsertion;
import operators.operators.WorstRemovalRegretKInsertion;
import operators.removalHeuristics.RandomRemoval;
import operators.removalHeuristics.RelatedRemoval;
import operators.removalHeuristics.WorstRemoval;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Constants {

    // EXAM INSTANCES

    public static final String C7V3_EXAM = "src/main/resources/exam/Call_7_Vehicle_3.txt";
    public static final String C18V5_EXAM = "src/main/resources/exam/Call_18_Vehicle_5.txt";
    public static final String C35V7_EXAM = "src/main/resources/exam/Call_35_Vehicle_7.txt";
    public static final String C80V20_EXAM = "src/main/resources/exam/Call_80_Vehicle_20.txt";
    public static final String C130V40_EXAM = "src/main/resources/exam/Call_130_Vehicle_40.txt";
    public static final List<String> INSTANCES_EXAM = Arrays.asList(C7V3_EXAM, C18V5_EXAM, C35V7_EXAM, C80V20_EXAM, C130V40_EXAM);

    // INSTANCES

    public static final String C7V3 = "src/main/resources/assignment/Call_7_Vehicle_3.txt";
    public static final String C18V5 = "src/main/resources/assignment/Call_18_Vehicle_5.txt";
    public static final String C35V7 = "src/main/resources/assignment/Call_35_Vehicle_7.txt";
    public static final String C80V20 = "src/main/resources/assignment/Call_80_Vehicle_20.txt";
    public static final String C130V40 = "src/main/resources/assignment/Call_130_Vehicle_40.txt";
    public static final List<String> INSTANCES = Arrays.asList(C7V3, C18V5, C35V7, C80V20, C130V40);

    // SEARCHING ALGORITHMS

    public static final SearchingAlgorithm RANDOM_SEARCH = new RandomSearch();
    public static final SearchingAlgorithm LOCAL_SEARCH = new LocalSearch();
    public static final SearchingAlgorithm SIMULATED_ANNEALING = new SimulatedAnnealing();
    public static final SearchingAlgorithm ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH = new AdaptiveLargeNeighbourhoodSearch();
    public static final List<SearchingAlgorithm> SEARCHING_ALGORITHMS = Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING, ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH);

    // OPERATORS

    public static final Operator randomOperator = new RandomOperator();

    public static final Operator oneInsert = new OneInsert();
    public static final Operator twoExchange = new TwoExchange();
    public static final Operator threeExchange = new ThreeExchange();

    public static final Operator randomRemovalGreedyInsertion = new RandomRemovalGreedyInsertion();
    public static final Operator randomRemovalRegretKInsertion = new RandomRemovalRegretKInsertion();
    public static final Operator relatedRemovalGreedyInsertion = new RelatedRemovalGreedyInsertion();
    public static final Operator relatedRemovalRegretKInsertion = new RelatedRemovalRegretKInsertion();
    public static final Operator worstRemovalGreedyInsertion = new WorstRemovalGreedyInsertion();
    public static final Operator worstRemovalRegretKInsertion = new WorstRemovalRegretKInsertion();

    // INSERTION AND REMOVAL HEURISTICS

    public static final GreedyInsertion greedyInsertion = new GreedyInsertion();
    public static final RegretKInsertion regretKInsertion = new RegretKInsertion();
    public static final RandomRemoval randomRemoval = new RandomRemoval();
    public static final WorstRemoval worstRemoval = new WorstRemoval();
    public static final RelatedRemoval relatedRemoval = new RelatedRemoval();

    // SEARCH CONSTANTS

    public static final boolean VISUALIZE = false;
    public static final boolean ITERATION_SEARCH = false;
    public static final int ITERATIONS = 35000;
    public static final int SEARCH_TIMES = 10;
    public static final double RUN_TIME_C7V3 = 10 * 0.999;
    public static final double RUN_TIME_C18V5 = 30 * 0.999;
    public static final double RUN_TIME_C35V7 = 80 * 0.999;
    public static final double RUN_TIME_C80V20 = 140 * 0.999;
    public static final double RUN_TIME_C130V40 = 340 * 0.999;

    public static final Random random = new Random();

}