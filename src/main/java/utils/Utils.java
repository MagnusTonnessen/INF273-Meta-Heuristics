package utils;

import objects.Results;
import objects.Solution;
import objects.Vehicle;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static main.Main.initialCost;
import static main.Main.instanceName;
import static main.Main.problem;

public class Utils {

    public static final int pad = 20;

    // Print tools
    public static void printRunInfo() {
        System.out.println("\n--- " + instanceName + " ---\n");
        System.out.printf("Initial cost: %.2f\n\n", initialCost);

        System.out.println(
                rightPad("", pad + 25) +
                        rightPad("Average objective", pad) +
                        rightPad("Best objective", pad) +
                        rightPad("Improvement (%)", pad) +
                        rightPad("Running time (seconds)", pad)
        );
    }

    public static void printRunResults(String algorithmName, Results results) {
        DecimalFormat format = new DecimalFormat("0.00#");

        System.out.println("\r" +
                rightPad(algorithmName, pad + 25) +
                rightPad(format.format(results.averageObjective()), pad) +
                rightPad(format.format(results.bestObjective()), pad) +
                rightPad(format.format(results.improvement()), pad) +
                rightPad(format.format(results.averageRunTime()), pad)
        );
    }

    public static String rightPad(String text, int length) {
        return String.format("%1$-" + length + "s", text);
    }

    public static String getAlgorithmName(Method algorithm) {
        if (algorithm.getName().contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.getName().contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.getName().split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getAlgorithmName(String algorithm) {
        if (algorithm.contains("simulated")) {
            return "Simulated Annealing (" + (algorithm.contains("New") ? "with new operators" : "old") + ")";
        }
        return Arrays
                .stream(algorithm.split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(joining(" "));
    }

    public static String getInstanceName(String filePath) {
        return filePath
                .replace("src/main/resources/", "")
                .replace("_", " ")
                .replace(".txt", "");
    }

    // PDP tools

    /**
     * @param solution solution to check feasibility of
     * @return true if solution is feasible
     */
    public static boolean feasibilityCheck(int[] solution) {

        int nVehicles = problem.nVehicles;
        int[] vesselCapacity = problem.vesselCapacity;
        int[][] cargo = problem.cargo;
        int[][] firstTravelTime = problem.firstTravelTime;
        int[][] loadingTime = problem.loadingTime;
        int[][] unloadingTime = problem.unloadingTime;
        int[][] vesselCargo = problem.vesselCargo;
        int[][][] travelTime = problem.travelTime;

        int[] sol = IntStream.range(0, solution.length + 1).map(i -> i < solution.length ? solution[i] : 0).toArray();
        int[] zeroIndex = IntStream.range(0, sol.length).filter(i -> sol[i] == 0).toArray();

        int tempIdx = 0;

        for (int i = 0; i < nVehicles; i++) {
            int finalI = i;

            int[] currentVPlan = Arrays.stream(sol, tempIdx, zeroIndex[i]).map(j -> j - 1).toArray();
            int noDoubleCallOnVehicle = currentVPlan.length;
            tempIdx = zeroIndex[i] + 1;

            if (noDoubleCallOnVehicle > 0) {
                if (Arrays.stream(currentVPlan).anyMatch(j -> vesselCargo[finalI][j] == 0)) {
                    return false;
                } else {
                    int currentTime = 0;
                    int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                    int[] index = argSort(argSort(currentVPlan));

                    int[] loadSizeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 1 ? -1 : 1) * cargo[sortRoute[j]][2]).toArray();
                    int[] loadSize = IntStream.range(0, sortRoute.length).map(j -> loadSizeSorted[index[j]]).toArray();

                    Arrays.parallelPrefix(loadSize, Integer::sum);
                    if (IntStream.range(0, loadSize.length).anyMatch(j -> vesselCapacity[finalI] - loadSize[j] < 0)) {
                        return false;
                    }

                    int[][] timeWindowsSorted = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> cargo[sortRoute[k]][(k % 2 == 0 ? 4 : 6) + j]).toArray()).toArray(int[][]::new);
                    int[][] timeWindows = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> timeWindowsSorted[j][index[k]]).toArray()).toArray(int[][]::new);

                    int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(j -> cargo[sortRoute[j]][j % 2]).toArray();
                    int[] portIndex = IntStream.range(0, sortRoute.length).map(j -> portIndexSorted[index[j]] - 1).toArray();

                    int[] LUTimeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 0 ? loadingTime : unloadingTime)[finalI][sortRoute[j]]).toArray();
                    int[] LUTime = IntStream.range(0, sortRoute.length).map(j -> LUTimeSorted[index[j]]).toArray();

                    int[] diag = IntStream.range(0, portIndex.length - 1).map(j -> travelTime[finalI][portIndex[j]][portIndex[j + 1]]).toArray();
                    int firstVisitTime = firstTravelTime[finalI][cargo[currentVPlan[0]][0] - 1];
                    int[] routeTravelTime = IntStream.range(0, diag.length + 1).map(j -> (j > 0 ? diag[j - 1] : firstVisitTime)).toArray();

                    int[] arriveTime = new int[noDoubleCallOnVehicle];
                    for (int j = 0; j < noDoubleCallOnVehicle; j++) {
                        arriveTime[j] = Math.max(currentTime + routeTravelTime[j], timeWindows[0][j]);
                        if (arriveTime[j] > timeWindows[1][j]) {
                            return false;
                        }
                        currentTime = arriveTime[j] + LUTime[j];
                    }
                }
            }
        }
        return true;
    }

    public static boolean feasibilityCheck(Solution solution) {

        int nVehicles = problem.nVehicles;
        int[][] cargo = problem.cargo;
        int[][] firstTravelTime = problem.firstTravelTime;
        int[][] loadingTime = problem.loadingTime;
        int[][] unloadingTime = problem.unloadingTime;
        int[][][] travelTime = problem.travelTime;

        for (int VIdx = 0; VIdx < nVehicles; VIdx++) {

            Vehicle vehicle = solution.get(VIdx);

            int[] currentVPlan = vehicle.asArray();
            int noDoubleCallOnVehicle = currentVPlan.length;

            if (noDoubleCallOnVehicle > 0) {
                int finalVehicle = VIdx;
                if (!Arrays.stream(currentVPlan).allMatch(vehicle.validCalls::contains)) {
                    return false;
                } else {
                    int currentTime = 0;
                    int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                    int[] index = argSort(argSort(currentVPlan));

                    int[] loadSizeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 1 ? -1 : 1) * cargo[sortRoute[j]][2]).toArray();
                    int[] loadSize = IntStream.range(0, sortRoute.length).map(j -> loadSizeSorted[index[j]]).toArray();

                    Arrays.parallelPrefix(loadSize, Integer::sum);
                    if (IntStream.range(0, loadSize.length).anyMatch(j -> vehicle.capacity - loadSize[j] < 0)) {
                        return false;
                    }

                    int[][] timeWindowsSorted = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> cargo[sortRoute[k]][(k % 2 == 0 ? 4 : 6) + j]).toArray()).toArray(int[][]::new);
                    int[][] timeWindows = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> timeWindowsSorted[j][index[k]]).toArray()).toArray(int[][]::new);

                    int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(j -> cargo[sortRoute[j]][j % 2]).toArray();
                    int[] portIndex = IntStream.range(0, sortRoute.length).map(j -> portIndexSorted[index[j]] - 1).toArray();

                    int[] LUTimeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 0 ? loadingTime : unloadingTime)[finalVehicle][sortRoute[j]]).toArray();
                    int[] LUTime = IntStream.range(0, sortRoute.length).map(j -> LUTimeSorted[index[j]]).toArray();

                    int[] diag = IntStream.range(0, portIndex.length - 1).map(j -> travelTime[finalVehicle][portIndex[j]][portIndex[j + 1]]).toArray();
                    int firstVisitTime = firstTravelTime[finalVehicle][cargo[currentVPlan[0]][0] - 1];
                    int[] routeTravelTime = IntStream.range(0, diag.length + 1).map(j -> (j > 0 ? diag[j - 1] : firstVisitTime)).toArray();

                    int[] arriveTime = new int[noDoubleCallOnVehicle];
                    for (int j = 0; j < noDoubleCallOnVehicle; j++) {
                        arriveTime[j] = Math.max(currentTime + routeTravelTime[j], timeWindows[0][j]);
                        if (arriveTime[j] > timeWindows[1][j]) {
                            return false;
                        }
                        currentTime = arriveTime[j] + LUTime[j];
                    }
                }
            }
        }
        return true;
    }

    public static boolean feasibilityCheck(Vehicle vehicle) {

        int[][] cargo = problem.cargo;
        int[][] firstTravelTime = problem.firstTravelTime;
        int[][] loadingTime = problem.loadingTime;
        int[][] unloadingTime = problem.unloadingTime;
        int[][][] travelTime = problem.travelTime;

        int[] currentVPlan = vehicle.asArray();
        int calls = vehicle.size();

        if (calls > 0) {
            int finalVehicle = vehicle.vehicleIndex;
            if (!Arrays.stream(currentVPlan).allMatch(vehicle.validCalls::contains)) {
                return false;
            } else {
                int currentTime = 0;
                int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                int[] index = argSort(argSort(currentVPlan));

                int[] loadSizeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 1 ? -1 : 1) * cargo[sortRoute[j]][2]).toArray();
                int[] loadSize = IntStream.range(0, sortRoute.length).map(j -> loadSizeSorted[index[j]]).toArray();

                Arrays.parallelPrefix(loadSize, Integer::sum);
                if (IntStream.range(0, loadSize.length).anyMatch(j -> vehicle.capacity - loadSize[j] < 0)) {
                    return false;
                }

                int[][] timeWindowsSorted = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> cargo[sortRoute[k]][(k % 2 == 0 ? 4 : 6) + j]).toArray()).toArray(int[][]::new);
                int[][] timeWindows = IntStream.range(0, 2).mapToObj(j -> IntStream.range(0, sortRoute.length).map(k -> timeWindowsSorted[j][index[k]]).toArray()).toArray(int[][]::new);

                int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(j -> cargo[sortRoute[j]][j % 2]).toArray();
                int[] portIndex = IntStream.range(0, sortRoute.length).map(j -> portIndexSorted[index[j]] - 1).toArray();

                int[] LUTimeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 0 ? loadingTime : unloadingTime)[finalVehicle][sortRoute[j]]).toArray();
                int[] LUTime = IntStream.range(0, sortRoute.length).map(j -> LUTimeSorted[index[j]]).toArray();

                int[] diag = IntStream.range(0, portIndex.length - 1).map(j -> travelTime[finalVehicle][portIndex[j]][portIndex[j + 1]]).toArray();
                int firstVisitTime = firstTravelTime[finalVehicle][cargo[currentVPlan[0]][0] - 1];
                int[] routeTravelTime = IntStream.range(0, diag.length + 1).map(j -> (j > 0 ? diag[j - 1] : firstVisitTime)).toArray();

                int[] arriveTime = new int[calls];
                for (int call = 0; call < calls; call++) {
                    arriveTime[call] = Math.max(currentTime + routeTravelTime[call], timeWindows[0][call]);
                    if (arriveTime[call] > timeWindows[1][call]) {
                        return false;
                    }
                    currentTime = arriveTime[call] + LUTime[call];
                }
            }
        }
        return true;
    }

    /**
     * @param solution solution to calculate cost of
     * @return cost of solution
     */
    public static double costFunction(int[] solution) {

        int nVehicles = problem.nVehicles;
        int[][] cargo = problem.cargo;
        int[][] firstTravelCost = problem.firstTravelCost;
        int[][] portCost = problem.portCost;
        int[][][] travelCost = problem.travelCost;

        double notTransportCost = 0;
        double[] routeTravelCost = new double[nVehicles];
        double[] costInPorts = new double[nVehicles];

        int[] sol = IntStream.range(0, solution.length + 1).map(i -> i < solution.length ? solution[i] : 0).toArray();
        int[] zeroIndex = IntStream.range(0, sol.length).filter(i -> sol[i] == 0).toArray();
        int tempIdx = 0;

        for (int i = 0; i < nVehicles + 1; i++) {
            int finalI = i;

            int[] currentVPlan = Arrays.stream(sol, tempIdx, zeroIndex[i]).map(j -> j - 1).toArray();
            int noDoubleCallOnVehicle = currentVPlan.length;
            tempIdx = zeroIndex[i] + 1;

            if (i == nVehicles) {
                notTransportCost = Arrays.stream(currentVPlan).map(j -> cargo[j][3]).sum() / 2.0;
            } else {
                if (noDoubleCallOnVehicle > 0) {
                    int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                    int[] index = argSort(argSort(currentVPlan));

                    int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(j -> cargo[sortRoute[j]][j % 2]).toArray();
                    int[] portIndex = IntStream.range(0, sortRoute.length).map(j -> portIndexSorted[index[j]] - 1).toArray();
                    int[] diag = IntStream.range(0, portIndex.length - 1).map(j -> travelCost[finalI][portIndex[j]][portIndex[j + 1]]).toArray();

                    int firstVisitCost = firstTravelCost[finalI][cargo[currentVPlan[0]][0] - 1];

                    routeTravelCost[i] = firstVisitCost + Arrays.stream(diag).sum();
                    costInPorts[i] = Arrays.stream(currentVPlan).map(j -> portCost[finalI][j]).sum() / 2.0;
                }
            }
        }
        return notTransportCost + Arrays.stream(routeTravelCost).sum() + Arrays.stream(costInPorts).sum();
    }

    public static double costFunction(Solution solution) {

        int nVehicles = problem.nVehicles;
        int[][] cargo = problem.cargo;
        int[][] firstTravelCost = problem.firstTravelCost;
        int[][] portCost = problem.portCost;
        int[][][] travelCost = problem.travelCost;

        double notTransportCost = 0;
        double[] routeTravelCost = new double[nVehicles];
        double[] costInPorts = new double[nVehicles];

        for (int VIdx = 0; VIdx < nVehicles + 1; VIdx++) {
            Vehicle vehicle = solution.get(VIdx);

            int[] currentVPlan = vehicle.asArray();

            if (VIdx == nVehicles) {
                notTransportCost = Arrays.stream(currentVPlan).map(j -> cargo[j][3]).sum() / 2.0;
            } else {
                if (vehicle.size() > 0) {
                    int finalVIdx = VIdx;
                    int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                    int[] index = argSort(argSort(currentVPlan));

                    int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(j -> cargo[sortRoute[j]][j % 2]).toArray();
                    int[] portIndex = IntStream.range(0, sortRoute.length).map(j -> portIndexSorted[index[j]] - 1).toArray();
                    int[] diag = IntStream.range(0, portIndex.length - 1).map(j -> travelCost[finalVIdx][portIndex[j]][portIndex[j + 1]]).toArray();

                    int firstVisitCost = firstTravelCost[finalVIdx][cargo[currentVPlan[0]][0] - 1];

                    routeTravelCost[VIdx] = firstVisitCost + Arrays.stream(diag).sum();
                    costInPorts[VIdx] = Arrays.stream(currentVPlan).map(j -> portCost[finalVIdx][j]).sum() / 2.0;
                }
            }
        }
        return notTransportCost + Arrays.stream(routeTravelCost).sum() + Arrays.stream(costInPorts).sum();
    }

    public static double costFunction(Vehicle vehicle) {

        int[][] cargo = problem.cargo;
        int[][] firstTravelCost = problem.firstTravelCost;
        int[][] portCost = problem.portCost;
        int[][][] travelCost = problem.travelCost;

        double routeTravelCost = 0;
        double costInPorts = 0;

        int[] currentVPlan = vehicle.asArray();

        if (vehicle.vehicleIndex == -1) {
            return Arrays.stream(currentVPlan).map(call -> problem.calls.get(call).costNotTransport).sum() / 2.0;
        } else {
            if (vehicle.size() > 0) {
                int VIdx = vehicle.vehicleIndex;
                int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                int[] index = argSort(argSort(currentVPlan));

                int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(call -> cargo[sortRoute[call]][call % 2]).toArray();
                int[] portIndex = IntStream.range(0, sortRoute.length).map(call -> portIndexSorted[index[call]] - 1).toArray();
                int[] diag = IntStream.range(0, portIndex.length - 1).map(call -> travelCost[VIdx][portIndex[call]][portIndex[call + 1]]).toArray();

                int firstVisitCost = firstTravelCost[VIdx][cargo[currentVPlan[0]][0] - 1];

                routeTravelCost = firstVisitCost + Arrays.stream(diag).sum();
                costInPorts = Arrays.stream(currentVPlan).map(call -> portCost[VIdx][call]).sum() / 2.0;
            }
            return routeTravelCost + costInPorts;
        }
    }

    /**
     * @param array array to get shuffle indices from
     * @return Indices of element after sort
     */
    private static int[] argSort(final int[] array) {
        Integer[] indexes = IntStream.range(0, array.length).boxed().toArray(Integer[]::new);
        Arrays.sort(indexes, Comparator.comparingInt(i -> array[i]));
        return Arrays.stream(indexes).mapToInt(i -> i).toArray();
    }
}
