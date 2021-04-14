package utils;

import objects.Results;
import objects.Solution;
import objects.Vehicle;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

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

    public static String getInstanceName(String filePath) {
        return filePath
                .substring(filePath.lastIndexOf("/") + 1)
                .replace("_", " ")
                .replace(".txt", "");
    }

    // PDP tools

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

    public static int costFunction(Vehicle vehicle) {

        int[][] cargo = problem.cargo;
        int[][] firstTravelCost = problem.firstTravelCost;
        int[][] portCost = problem.portCost;
        int[][][] travelCost = problem.travelCost;

        int routeTravelCost = 0;
        int costInPorts = 0;

        int[] currentVPlan = vehicle.asArray();

        if (vehicle.vehicleIndex == problem.nVehicles) {
            return Arrays.stream(currentVPlan).map(call -> problem.calls.get(call).costNotTransport).sum() / 2;
        } else {
            if (vehicle.size() > 0) {
                int VIdx = vehicle.vehicleIndex;
                int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                int[] index = argSort(argSort(currentVPlan));

                int[] portIndexSorted = IntStream.range(0, sortRoute.length).map(call -> cargo[sortRoute[call]][call % 2]).toArray();
                int[] portIndex = IntStream.range(0, sortRoute.length).map(call -> portIndexSorted[index[call]] - 1).toArray();
                int[] diag = IntStream.range(0, portIndex.length - 1).map(call -> travelCost[VIdx][portIndex[call]][portIndex[call]]).toArray();

                int firstVisitCost = firstTravelCost[VIdx][cargo[currentVPlan[0]][0] - 1];

                routeTravelCost = firstVisitCost + Arrays.stream(diag).sum();
                costInPorts = Arrays.stream(currentVPlan).map(call -> portCost[VIdx][call]).sum() / 2;
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
