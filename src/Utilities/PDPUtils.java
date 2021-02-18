package Utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class PDPUtils {

    public static final Random random = new Random();

    /**
     * Load problem into map
     * @param filename Path of problem file
     * @return Map with problem info
     */
    public static Map<String, Object> loadProblem(String filename) {

        Map<String, Object> problemMap = new HashMap<>();

        try {
            int nNodes;
            int nVehicles;
            int nCalls;

            int[][] vehicle;
            int[][] validCalls;
            int[][] nodeTimesAndCosts;

            int[] vesselCapacity;
            int[][] cargo;
            int[][] firstTravelTime;
            int[][] firstTravelCost;
            int[][] loadingTime;
            int[][] unloadingTime;
            int[][] vesselCargo;
            int[][] portCost;
            int[][][] travelTime;
            int[][][] travelCost;

            List<String> input = Files.readAllLines(Path.of(filename));

            nNodes = Integer.parseInt(input.get(1));
            nVehicles = Integer.parseInt(input.get(3));
            nCalls = Integer.parseInt(input.get(nVehicles + 6));

            vehicle = IntStream
                        .range(0, nVehicles)
                        .mapToObj(i -> Arrays
                            .stream(input.get(1 + 4 + i).split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                        .toArray(int[][]::new);

            validCalls = IntStream
                            .range(0, nVehicles)
                            .mapToObj(i -> Arrays
                                .stream(input.get(1 + 7 + nVehicles + i).split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray())
                            .toArray(int[][]::new);

            vesselCargo = new int[nVehicles][nCalls];
            IntStream.range(0, nVehicles).forEach(i -> Arrays.stream(validCalls[i]).forEach(j -> vesselCargo[i][j - 1] = 1));

            cargo = IntStream
                        .range(0, nCalls)
                        .mapToObj(i -> Arrays
                            .stream(input.get(1 + 8 + nVehicles * 2 + i).split(","))
                            .skip(1)
                            .mapToInt(Integer::parseInt)
                            .toArray())
                        .toArray(int[][]::new);

            travelCost = new int[nVehicles][nNodes][nNodes];
            travelTime = new int[nVehicles][nNodes][nNodes];

            IntStream
                .range(0, nNodes * nNodes * nVehicles)
                .mapToObj(i -> Arrays
                    .stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray())
                .forEach(arr -> {
                    travelTime[arr[0] - 1][arr[1] - 1][arr[2] - 1] = arr[3];
                    travelCost[arr[0] - 1][arr[1] - 1][arr[2] - 1] = arr[4];
                });

            nodeTimesAndCosts = IntStream
                                    .range(0, nVehicles  * nCalls)
                                    .mapToObj(i -> Arrays
                                        .stream(input.get(1 + 1 + 2 * nVehicles + nCalls + 10 + nNodes * nNodes * nVehicles - 1 + i).split(","))
                                        .mapToInt(Integer::parseInt)
                                        .toArray())
                                    .toArray(int[][]::new);

            loadingTime = new int[nVehicles][nCalls];
            unloadingTime = new int[nVehicles][nCalls];
            portCost = new int[nVehicles][nCalls];

            IntStream.range(0, nVehicles * nCalls).forEach(i -> {
                loadingTime[nodeTimesAndCosts[i][0] - 1][nodeTimesAndCosts[i][1] - 1] = nodeTimesAndCosts[i][2];
                unloadingTime[nodeTimesAndCosts[i][0] - 1][nodeTimesAndCosts[i][1] - 1] = nodeTimesAndCosts[i][4];
                portCost[nodeTimesAndCosts[i][0] - 1][nodeTimesAndCosts[i][1] - 1] = nodeTimesAndCosts[i][3] + nodeTimesAndCosts[i][5];
            });

            vesselCapacity = new int[nVehicles];
            firstTravelTime = new int[nVehicles][nNodes];
            firstTravelCost = new int[nVehicles][nNodes];

            IntStream.range(0, nVehicles).forEach(i -> {
                vesselCapacity[i] = vehicle[i][3];
                IntStream.range(0, nNodes).forEach(j -> {
                    firstTravelTime[i][j] = travelTime[i][vehicle[i][1] - 1][j] + vehicle[i][2];
                    firstTravelCost[i][j] = travelCost[i][vehicle[i][1] - 1][j];
                });
            });

            problemMap.put("nNodes", nNodes);
            problemMap.put("nVehicles", nVehicles);
            problemMap.put("nCalls", nCalls);
            problemMap.put("vesselCapacity", vesselCapacity);
            problemMap.put("cargo", cargo);
            problemMap.put("firstTravelTime", firstTravelTime);
            problemMap.put("firstTravelCost", firstTravelCost);
            problemMap.put("loadingTime", loadingTime);
            problemMap.put("unloadingTime", unloadingTime);
            problemMap.put("vesselCargo", vesselCargo);
            problemMap.put("portCost", portCost);
            problemMap.put("travelTime", travelTime);
            problemMap.put("travelCost", travelCost);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return problemMap;
    }

    /**
     *
     * @param solution solution to check feasibility of
     * @param problem problem info
     * @return true if solution is feasible
     */
    public static boolean feasibilityCheck(int[] solution, Map<String, Object> problem) {

        int nVehicles = (int) problem.get("nVehicles");
        int[] vesselCapacity = (int[]) problem.get("vesselCapacity");
        int[][] cargo = (int[][]) problem.get("cargo");
        int[][] firstTravelTime = (int[][]) problem.get("firstTravelTime");
        int[][] loadingTime = (int[][]) problem.get("loadingTime");
        int[][] unloadingTime = (int[][]) problem.get("unloadingTime");
        int[][] vesselCargo = (int[][]) problem.get("vesselCargo");
        int[][][] travelTime = (int[][][]) problem.get("travelTime");

        int[] sol = IntStream.range(0, solution.length + 1).map(i -> i < solution.length ? solution[i] : 0).toArray();
        int[] zeroIndex = IntStream.range(0, sol.length).filter(i -> sol[i] == 0).toArray();

        boolean feasibility = true;
        int tempIdx = 0;
        String errorMessage = "Feasible";

        for (int i = 0; i < nVehicles; i++) {
            int finalI = i;

            int[] currentVPlan = Arrays.stream(sol, tempIdx, zeroIndex[i]).map(j -> j - 1).toArray();
            int noDoubleCallOnVehicle = currentVPlan.length;
            tempIdx = zeroIndex[i] + 1;

            if (noDoubleCallOnVehicle > 0) {
                if (Arrays.stream(currentVPlan).anyMatch(j -> vesselCargo[finalI][j] == 0)) {
                    return false;
                    /*
                    feasibility = false;
                    errorMessage = "Incompatible vessel and cargo";
                    break;
                    */
                } else {
                    int currentTime = 0;
                    int[] sortRoute = Arrays.stream(currentVPlan).sorted().toArray();
                    int[] index = argSort(argSort(currentVPlan));

                    int[] loadSizeSorted = IntStream.range(0, sortRoute.length).map(j -> (j % 2 == 1 ? -1 : 1) * cargo[sortRoute[j]][2]).toArray();
                    int[] loadSize = IntStream.range(0, sortRoute.length).map(j -> loadSizeSorted[index[j]]).toArray();

                    Arrays.parallelPrefix(loadSize, Integer::sum);
                    if (IntStream.range(0, loadSize.length).anyMatch(j -> vesselCapacity[finalI] - loadSize[j] < 0)) {
                        return false;
                        /*
                        feasibility = false;
                        errorMessage = "Capacity exceeded";
                        break;
                        */
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
                            /*
                            feasibility = false;
                            errorMessage = "Time window exceeded at call " + j;
                            break;
                            */
                        }
                        currentTime = arriveTime[j] + LUTime[j];
                    }
                }
            }
        }
        return feasibility;
    }

    /**
     *
     * @param solution solution to calculate cost of
     * @param problem problem info
     * @return cost of solution
     */
    public static double costFunction(int[] solution, Map<String, Object> problem) {

        int nVehicles = (int) problem.get("nVehicles");
        int[][] cargo = (int[][]) problem.get("cargo");
        int[][] firstTravelCost = (int[][]) problem.get("firstTravelCost");
        int[][] portCost = (int[][]) problem.get("portCost");
        int[][][] travelCost = (int[][][]) problem.get("travelCost");

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

    /**
     * Shuffle int array
     * @param array array to shuffle
     */
    public static void shuffle(int[] array) {
        for (int i = 0; i < array.length; ++i) {
            int index = random.nextInt(array.length - i);
            int tmp = array[array.length - 1 - i];
            array[array.length - 1 - i] = array[index];
            array[index] = tmp;
        }
    }

    /**
     *
     * @param array array to get shuffle indices from
     * @return Indices of element after sort
     */
    private static int[] argSort(final int[] array) {
        Integer[] indexes = IntStream.range(0, array.length).boxed().toArray(Integer[]::new);
        Arrays.sort(indexes, Comparator.comparingInt(i -> array[i]));
        return Arrays.stream(indexes).mapToInt(i -> i).toArray();
    }

    public static int[] generateInitSolution(Map<String, Object> problem) {
        int nCalls = (int) problem.get("nCalls");
        int nVehicles = (int) problem.get("nVehicles");
        int[] initSol = new int[2 * nCalls + nVehicles];
        IntStream.range(0, nCalls * 2).forEach(i -> initSol[i + nVehicles] = (i + 2)/2);
        return initSol;
    }
}
