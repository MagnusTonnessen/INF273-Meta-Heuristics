package utils;

import algorithms.AdaptiveLargeNeighbourhoodSearch;
import algorithms.LocalSearch;
import algorithms.RandomSearch;
import algorithms.SearchingAlgorithm;
import algorithms.SimulatedAnnealing;
import algorithms.SimulatedAnnealingNewOperators;
import operators.insertionHeuristics.GreedyInsertion;
import operators.insertionHeuristics.InsertionHeuristic;
import operators.insertionHeuristics.RegretKInsertion;
import operators.removalHeuristics.RandomRemoval;
import operators.removalHeuristics.RelatedRemoval;
import operators.removalHeuristics.RemovalHeuristic;
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
    public static final SearchingAlgorithm SIMULATED_ANNEALING_NEW_OPERATORS = new SimulatedAnnealingNewOperators();
    public static final SearchingAlgorithm ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH = new AdaptiveLargeNeighbourhoodSearch();
    public static final List<SearchingAlgorithm> SEARCHING_ALGORITHMS = Arrays.asList(RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING, SIMULATED_ANNEALING_NEW_OPERATORS, ADAPTIVE_LARGE_NEIGHBOURHOOD_SEARCH);

    // INSERTION AND REMOVAL HEURISTICS

    public static final InsertionHeuristic greedyInsertion = new GreedyInsertion();
    public static final InsertionHeuristic regretKInsertion = new RegretKInsertion();
    public static final RemovalHeuristic randomRemoval = new RandomRemoval();
    public static final RemovalHeuristic worstRemoval = new WorstRemoval();
    public static final RemovalHeuristic relatedRemoval = new RelatedRemoval();

    // SEARCH CONSTANTS

    public static final boolean ITERATION_SEARCH = true;
    public static final int ITERATIONS = 10000;
    public static final int SEARCH_TIMES = 1;
    public static final double RUN_TIME_C7V3 = 10 * 0.99;
    public static final double RUN_TIME_C18V5 = 30 * 0.99;
    public static final double RUN_TIME_C35V7 = 80 * 0.99;
    public static final double RUN_TIME_C80V20 = 140 * 0.99;
    public static final double RUN_TIME_C130V40 = 340 * 0.99;

    public static final Random random = new Random();

    public static final String TRANSPORT_ALL_TITLE = "Transport all operator";
    public static final String TRANSPORT_ALL_DESCRIPTION =
            """
                    Not transporting a call is the most expensive option for a call.
                    thus this operator aims to distribute all calls to a valid vehicle.
                    All not transported calls are sorted ascending according to number of vehicles that can pickup/deliver it,
                    to prevent a call that has many valid vehicles to steal a spot from a call with few valid vehicles.
                    The operator will try to find a feasible vehicle insert for a call.
                                
                    Pseudo code:
                                
                    ===================================
                    calls = sort call asc by number of valid vehicles
                                
                    for call in calls
                        vehicles = valid vehicles for call
                        for vehicle in vehicles
                            insert call in vehicle
                            if new solution is feasible
                                return new solution
                                
                    return original solution
                    ===================================
                                
                    Early searchingAlgorithm this operator is fast, because there are many feasible solutions in the neighbourhood.
                    Late searchingAlgorithm it is very slow, because it has to check all combinations of
                    not transported calls and valid vehicles and will probably return the original solution.
                                
                    I think this operator leads to diversification,
                    because it moves a call from one vehicle (dummy) to another vehicle
                                
                    Potential for improvement:
                    Sort vehicles by cost of transporting call
                    """;
    public static final String REINSERT_MOST_EXPENSIVE_TITLE = "Reinsert most expensive operator";
    public static final String REINSERT_MOST_EXPENSIVE_DESCRIPTION =
            """
                    This operator aims to reduce objective of solution
                    by moving a call from one of the three most expensive vehicles to one of the three least expensive vehicles
                    To prevent getting stuck, the operator:
                        1. shuffles the order of the three most expensive vehicles before iterating,
                        2. shuffles the order of the the calls in that vehicle
                        3. shuffles the order of the three least expensive valid vehicles
                                
                    Pseudo code:
                                
                    ===================================
                    fromVehicles = three most expensive vehicles
                    shuffle fromVehicles
                                
                    for fromVehicle in fromVehicle
                        calls = calls in fromVehicle
                        shuffle calls
                        
                        for call in calls
                            toVehicles = three least expensive vehicles
                            shuffle toVehicles
                            
                            for toVehicle in toVehicles
                                insert call in toVehicle
                                if new solution is feasible
                                    return new solution
                    ===================================
                                
                    Early searchingAlgorithm this operator is very fast, but very ineffective,
                    because it will move calls random between vehicles, and not necessarily result in a better solution.
                    Late game the operator is time expensive, but can reduce objective of solution a lot.
                                
                    This operator leads to diversification of the solution,
                    because it moves a call from one vehicle to another.
                                
                    Potential for improvement:
                    Triple for loop yuck <|:^)
                    Reduce the randomness of the operator.
                    Improve choice of call to relocate and insert vehicle
                    """;
    public static final String BRUTE_FORCE_VEHICLE_TITLE = "Brute force vehicle operator";
    public static final String BRUTE_FORCE_VEHICLE_DESCRIPTION =
            """
                    Transport all and reinsert from most expensive inserts call in the front/back of the vehicle.
                    This operator aims to find a feasible permutation of a vehicle with a lower cost than the original solution.
                    Because finding permutations is extremely time expensive, this operator is limited to changing vehicles with 2 to 4 calls
                                
                    Pseudo code:
                                
                    =========================================
                    vehicle = random vehicle with 2 to 4 calls
                                
                    check objective for all permutations of calls in vehicle
                                
                    return solution with best objective
                    =========================================
                                
                    Early searchingAlgorithm will this operator often return the original solution,
                    because there are no vehicles with two or more calls or the selected vehicle is already optimal.
                    Late searchingAlgorithm this operator is time expensive, but always finds the best solution for a vehicle.
                                
                    This operator leads to intensification of the solution,
                    because it aims to improve a part of the solution.
                                
                    Potential for improvement:
                    Find a better way to pick which vehicle to brute force,
                    for example by choosing from a queue of last changed vehicles.
                    Operator is limited to vehicles with few calls
                    """;
}
/*
Diversification is the method of identifying diverse promising regions
of the searchingAlgorithm space and is achieved through the process of heating/reheating.

Intensification is the method of finding a solution in one of these promising
regions and is achieved through the process of cooling.
 */