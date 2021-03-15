package utils;

import objects.Call;
import objects.NodeTimeAndCost;
import objects.TravelTimeAndCost;
import objects.Vehicle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static utils.Constants.random;
import static utils.Utils.getInstanceName;

public class PDPUtils {

    public static String instanceName;
    public static Problem problem;
    public static int[] initialSolution;
    public static double initialCost;

    public static void initialize(String filePath) throws Exception {
        instanceName = getInstanceName(filePath);
        problem = loadProblem(filePath);
        initialSolution = problem.initialSolution;
        initialCost = problem.initialCost;
    }

    /**
     * Load problem into map
     *
     * @param filePath Path of problem file
     * @return Map with problem info
     */
    public static Problem loadProblem(String filePath) throws Exception {

        int nNodes;
        int nVehicles;
        int nCalls;

        int[][] validCalls;
        int[][] nodes;

        Vehicle[] vehicles;
        Call[] calls;
        TravelTimeAndCost[] travelTimeAndCosts;
        NodeTimeAndCost[] nodeTimeAndCosts;

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

        List<String> input = Files.readAllLines(Path.of(filePath));

        nNodes = Integer.parseInt(input.get(1));
        nVehicles = Integer.parseInt(input.get(3));
        nCalls = Integer.parseInt(input.get(nVehicles + 6));

        validCalls = IntStream
                .range(0, nVehicles)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 7 + nVehicles + i).split(","))
                        .skip(1)
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .toArray(int[][]::new);

        vehicles = IntStream
                .range(0, nVehicles)
                .mapToObj(i -> new Vehicle(Arrays
                        .stream(input.get(1 + 4 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray(), validCalls[i]))
                .toArray(Vehicle[]::new);

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

        calls = IntStream
                .range(0, nCalls)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 8 + nVehicles * 2 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .map(Call::new)
                .toArray(Call[]::new);

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

        travelTimeAndCosts = IntStream
                .range(0, nNodes * nNodes * nVehicles)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .map(TravelTimeAndCost::new)
                .toArray(TravelTimeAndCost[]::new);

        nodeTimeAndCosts = IntStream
                .range(0, nVehicles * nCalls)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 1 + 2 * nVehicles + nCalls + 10 + nNodes * nNodes * nVehicles - 1 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .map(NodeTimeAndCost::new)
                .toArray(NodeTimeAndCost[]::new);

        nodes = IntStream
                .range(0, nVehicles * nCalls)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 1 + 2 * nVehicles + nCalls + 10 + nNodes * nNodes * nVehicles - 1 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .toArray(int[][]::new);

        loadingTime = new int[nVehicles][nCalls];
        unloadingTime = new int[nVehicles][nCalls];
        portCost = new int[nVehicles][nCalls];

        IntStream.range(0, nVehicles * nCalls).forEach(i -> {
            loadingTime[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][2];
            unloadingTime[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][4];
            portCost[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][3] + nodes[i][5];
        });

        vesselCapacity = new int[nVehicles];
        firstTravelTime = new int[nVehicles][nNodes];
        firstTravelCost = new int[nVehicles][nNodes];

        IntStream.range(0, nVehicles).forEach(i -> {
            vesselCapacity[i] = vehicles[i].capacity;
            IntStream.range(0, nNodes).forEach(j -> {
                firstTravelTime[i][j] = travelTime[i][vehicles[i].homeNode - 1][j] + vehicles[i].startingTime;
                firstTravelCost[i][j] = travelCost[i][vehicles[i].homeNode - 1][j];
            });
        });

        return new Problem(nCalls, nVehicles, nNodes,
                vesselCapacity, cargo, firstTravelTime,
                firstTravelCost, loadingTime, unloadingTime,
                vesselCargo, portCost, travelTime, travelCost,
                vehicles, calls, travelTimeAndCosts, nodeTimeAndCosts);
    }

    public static boolean feasibilityCheck(int[] solution) {
        return feasibilityCheck(solution, problem);
    }

    /**
     * @param solution solution to check feasibility of
     * @param problem  problem info
     * @return true if solution is feasible
     */
    public static boolean feasibilityCheck(int[] solution, Problem problem) {

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

    public static double costFunction(int[] solution) {
        return costFunction(solution, problem);
    }

    /**
     * @param solution solution to calculate cost of
     * @param problem  problem info
     * @return cost of solution
     */
    public static double costFunction(int[] solution, Problem problem) {

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

    /**
     * Shuffle int array
     *
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
        IntStream.range(0, nCalls * 2).forEach(i -> initSol[i + nVehicles] = (i + 2) / 2);
        return initSol;
    }

    public static int[] generateInitSolution(int nCalls, int nVehicles) {
        int[] initSol = new int[2 * nCalls + nVehicles];
        IntStream.range(0, nCalls * 2).forEach(i -> initSol[i + nVehicles] = (i + 2) / 2);
        return initSol;
    }

    public static int[] validCallForVehicle(int vehicle) {
        int[] calls = problem.vesselCargo[vehicle - 1];
        return IntStream.range(0, calls.length).filter(call -> calls[call] == 1).toArray();
    }

    public static int[] validVehiclesForCall(int call) {
        int[] vehicles = Arrays.stream(problem.vesselCargo).mapToInt(vehicle -> vehicle[call - 1]).toArray();
        return IntStream.rangeClosed(0, vehicles.length).filter(vehicle -> vehicle == vehicles.length || vehicles[vehicle] == 1).toArray();
    }

    public static int[] transportedCalls(int[] solution) {
        List<Integer> notTransported = Arrays.stream(notTransportedCalls(solution)).boxed().collect(toList());
        return IntStream.rangeClosed(1, problem.nCalls).filter(i -> !notTransported.contains(i)).toArray();
    }

    public static int[] notTransportedCalls(int[] solution) {
        int lastVehicleIndex = IntStream.range(0, solution.length).filter(i -> solution[i] == 0).toArray()[problem.nVehicles - 1];
        return Arrays.stream(solution).skip(lastVehicleIndex + 1).distinct().toArray();
    }
}
