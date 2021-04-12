package objects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class Problem {
    public final int nCalls;
    public final int nVehicles;
    public final int nNodes;
    public final int[] vesselCapacity;
    public final int[][] cargo;
    public final int[][] firstTravelTime;
    public final int[][] firstTravelCost;
    public final int[][] loadingTime;
    public final int[][] unloadingTime;
    public final int[][] vesselCargo;
    public final int[][] portCost;
    public final int[][][] travelTime;
    public final int[][][] travelCost;
    public final List<Vehicle> vehicles;
    public final List<Call> calls;
    public final TravelTimeAndCost[] travelTimeAndCosts;
    public final Map<Integer, Map<Integer, Map<Integer, TravelTimeAndCost>>> travel;
    public final NodeTimeAndCost[] nodeTimeAndCosts;

    public Problem(String filePath) throws Exception {
        int[][] validCalls;
        int[][] nodes;

        List<String> input = Files.readAllLines(Path.of(filePath));

        nNodes = Integer.parseInt(input.get(1));
        nVehicles = Integer.parseInt(input.get(3));
        nCalls = Integer.parseInt(input.get(nVehicles + 6));

        validCalls = IntStream
                .range(0, nVehicles)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 7 + nVehicles + i).split(","))
                        .skip(1)
                        .mapToInt(c -> Integer.parseInt(c) - 1)
                        .toArray())
                .toArray(int[][]::new);

        vehicles = IntStream
                .range(0, nVehicles)
                .mapToObj(i -> new Vehicle(Arrays
                        .stream(input.get(1 + 4 + i).split(","))
                        .mapToInt(Integer::parseInt)
                        .toArray(), Arrays.stream(validCalls[i]).boxed().collect(toSet())))
                .collect(Collectors.toList());

        vesselCargo = new int[nVehicles][nCalls];
        IntStream.range(0, nVehicles).forEach(vehicle -> Arrays.stream(validCalls[vehicle]).forEach(call -> vesselCargo[vehicle][call] = 1));

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
                .map(call -> {
                    int[] validVehicles = IntStream.range(0, nVehicles).filter(vehicle -> Arrays.stream(validCalls[vehicle]).anyMatch(c -> c == call[0] - 1)).toArray();
                    return new Call(call, validVehicles);
                })
                .collect(Collectors.toList());

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

        travel = IntStream
                .range(0, nNodes * nNodes * nVehicles)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(","))
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new))
                .collect(groupingBy(vehicle -> vehicle[0] - 1, groupingBy(vehicle -> vehicle[1] - 1, toMap(vehicle -> vehicle[2] - 1, TravelTimeAndCost::new))));

        travelTimeAndCosts = IntStream
                .range(0, nNodes * nNodes * nVehicles)
                .mapToObj(i -> Arrays
                        .stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(","))
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new))
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
            vesselCapacity[i] = vehicles.get(i).capacity;
            IntStream.range(0, nNodes).forEach(j -> {
                firstTravelTime[i][j] = travelTime[i][vehicles.get(i).homeNode - 1][j] + vehicles.get(i).startingTime;
                firstTravelCost[i][j] = travelCost[i][vehicles.get(i).homeNode - 1][j];
            });
        });
    }

    public Call getCallFromIndex(int call) {
        return calls.get(call);
    }

    @Override
    public String toString() {
        return "Problem{" +
                "vehicles=" + vehicles +
                ", calls=" + calls +
                ", travelTimeAndCosts=" + Arrays.toString(travelTimeAndCosts) +
                ", nodeTimeAndCosts=" + Arrays.toString(nodeTimeAndCosts) +
                '}';
    }
}
