package utils;

import algorithms.SearchingAlgorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.toMap;

public class Constants {

    // INSTANCES

    public static final String C7V3 = "src/main/resources/Call_7_Vehicle_3.txt";
    public static final String C18V5 = "src/main/resources/Call_18_Vehicle_5.txt";
    public static final String C35V7 = "src/main/resources/Call_35_Vehicle_7.txt";
    public static final String C80V20 = "src/main/resources/Call_80_Vehicle_20.txt";
    public static final String C130V40 = "src/main/resources/Call_130_Vehicle_40.txt";
    public static final String[] INSTANCES = new String[]{C7V3, C18V5, C35V7, C80V20, C130V40};

    // SEARCHING ALGORITHMS
    public static final String RANDOM_SEARCH = "randomSearch";
    public static final String LOCAL_SEARCH = "localSearch";
    public static final String SIMULATED_ANNEALING = "simulatedAnnealing";
    public static final String SIMULATED_ANNEALING_NEW_OPERATORS = "simulatedAnnealingNewOperators";
    public static final String[] SEARCHING_ALGORITHMS = new String[]{RANDOM_SEARCH, LOCAL_SEARCH, SIMULATED_ANNEALING, SIMULATED_ANNEALING_NEW_OPERATORS};

    // NUMBER OF ITERATIONS PER SEARCH
    public static final int ITERATIONS = 10000;

    public static final SearchingAlgorithms searchingAlgorithms = new SearchingAlgorithms();
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
            
            Early search this operator is fast, because there are many feasible solutions in the neighbourhood.
            Late search it is very slow, because it has to check all combinations of 
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
            
            Early search this operator is very fast, but very ineffective,
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
            
            Early search will this operator often return the original solution,
            because there are no vehicles with two or more calls or the selected vehicle is already optimal.
            Late search this operator is time expensive, but always finds the best solution for a vehicle.
            
            This operator leads to intensification of the solution,
            because it aims to improve a part of the solution.
            
            Potential for improvement:
            Find a better way to pick which vehicle to brute force,
            for example by choosing from a queue of last changed vehicles.
            Operator is limited to vehicles with few calls
            """;


    public static final Map<String, Map<String, Map<String, Object>>> RESULTS_MAP = Arrays.stream(INSTANCES).collect(toMap(i -> i, i -> new HashMap<>()));
}
/*
Diversification is the method of identifying diverse promising regions
of the search space and is achieved through the process of heating/reheating.

Intensification is the method of finding a solution in one of these promising
regions and is achieved through the process of cooling.
 */