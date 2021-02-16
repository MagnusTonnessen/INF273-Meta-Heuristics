package Assignments;

import static Utils.PDPUtils.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

public class Assignment2 {

    String C7V3 = "Call_7_Vehicle_3.txt";
    String C18V5 = "Call_18_Vehicle_5.txt";
    String C35V7 = "Call_035_Vehicle_07.txt";
    String C80V20 = "Call_080_Vehicle_20.txt";
    String C130V40 = "Call_130_Vehicle_40.txt";

    String[] instances = new String[] {C7V3, C18V5, C35V7, C80V20, C130V40};

    public int[] generateInitSolution(Map<String, Object> problem) {
        int nCalls = (int) problem.get("nCalls");
        int nVehicles = (int) problem.get("nVehicles");
        int[] initSol = new int[2 * nCalls + nVehicles];
        IntStream.range(0, nCalls * 2).forEach(i -> initSol[i + nVehicles] = (i + 2)/2);
        return initSol;
    }

    public int[] validRandomSolution(Map<String, Object> problem) {
        int nCalls = (int) problem.get("nCalls");
        int nVehicles = (int) problem.get("nVehicles");
        int[] pickup = IntStream.range(1, nCalls + nVehicles + 1).map(i -> (i > nCalls ? 0 : i)).toArray();

        shuffle(pickup);

        int[] solution = new int[2 * nCalls + nVehicles];
        int[] zeroIndex = IntStream.range(0, pickup.length + 1).filter(i -> i >= pickup.length || pickup[i] == 0).toArray();
        int idx = 0;

        for (int i = 0; i < nVehicles + 1; i++) {
            int[] vehicle = Arrays.stream(pickup, idx, zeroIndex[i]).mapToObj(j -> new int[] {j, j}).flatMapToInt(Arrays::stream).toArray();
            shuffle(vehicle);
            System.arraycopy(vehicle, 0, solution, idx * 2 - i, vehicle.length);
            idx = zeroIndex[i] + 1;
        }

        return solution;
    }

    public int[] blindRandomSearch(int[] initSolution, Map<String, Object> problem) {
        int[] bestSolution = initSolution;
        double bestCost = costFunction(bestSolution, problem);
        for (int i = 0; i < 10000; i++) {
            int[] currentSolution = validRandomSolution(problem);
            double currentCost = costFunction(currentSolution, problem);
            if (feasibilityCheck(currentSolution, problem) && currentCost < bestCost) {
                bestSolution = currentSolution;
                bestCost = currentCost;
            }
        }
        return bestSolution;
    }

    public void runInstance(String fileName, int times) {
        System.out.println("\n--- Running Random Search ---\n");
        System.out.println(fileName.substring(0, fileName.length() - 4).replace("_", " ") + "\n");

        Map<String, Object> problem = loadProblem("src/main/resources/" + fileName);

        int[] initialSolution = generateInitSolution(problem);
        double initialCost = costFunction(initialSolution, problem);
        int[] bestSolution = initialSolution;
        double bestCost = initialCost;
        double totalCost = 0;
        double executionTime = 0;

        System.out.println("Initial solution: " + Arrays.toString(initialSolution));
        System.out.printf("Initial cost: %.2f\n\n", initialCost);

        for (int i = 0; i < times; i++) {
            System.out.print("\rProgress: " + (i+1) + "/" + times);
            long startTime = System.currentTimeMillis();
            int[] solution = blindRandomSearch(initialSolution, problem);
            executionTime += System.currentTimeMillis() - startTime;
            double cost = costFunction(solution, problem);
            totalCost += cost;
            if (cost < bestCost) {
                bestSolution = solution;
                bestCost = cost;
            }
        }
        System.out.printf("\n\nAverage cost: %.2f\n" , totalCost / times);
        System.out.printf("Best cost: %.2f\n", bestCost);
        System.out.println("Best solution: " + Arrays.toString(bestSolution));
        System.out.printf("Improvement: %.2f %%\n", (100.0 * (initialCost - bestCost) / initialCost));
        System.out.printf("Average execution time: %.3f seconds\n", (executionTime / times) / 1000);
        System.out.println("\n--- End of Random Search ---");
    }

    public void runAllInstances() {
        for (String instance :instances) {
            runInstance(instance, 10);
        }
    }
}
