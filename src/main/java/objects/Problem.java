package objects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Problem {
    public int maxCallSize = Integer.MIN_VALUE;
    public int minCallSize = Integer.MAX_VALUE;
    public double maxTravelDistance = Integer.MIN_VALUE;
    public double minTravelDistance = Integer.MAX_VALUE;
    public double maxPickupDeliveryTimeWindow = Integer.MIN_VALUE;
    public double minPickupDeliveryTimeWindow = Integer.MAX_VALUE;
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
                        .toArray(), Arrays.stream(validCalls[i]).boxed().collect(toSet()), false))
                .collect(Collectors.toList());

        vesselCargo = new int[nVehicles][nCalls];

        for (int vehicle = 0; vehicle < nVehicles; vehicle++) {
            for (int call = 0; call < validCalls[vehicle].length; call++) {
                vesselCargo[vehicle][call] = 1;
            }
        }

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
                .map(call -> new Call(call, IntStream.range(0, nVehicles).filter(vehicle -> Arrays.stream(validCalls[vehicle]).anyMatch(c -> c == (call[0] - 1))).boxed().collect(toList())))
                .collect(Collectors.toList());

        travelCost = new int[nVehicles][nNodes][nNodes];
        travelTime = new int[nVehicles][nNodes][nNodes];

        for (int i = 0; i < nNodes * nNodes * nVehicles; i++) {
            int[] arr = Arrays.stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(",")).mapToInt(Integer::parseInt).toArray();
            travelTime[arr[0] - 1][arr[1] - 1][arr[2] - 1] = arr[3];
            travelCost[arr[0] - 1][arr[1] - 1][arr[2] - 1] = arr[4];
        }

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

        for (int i = 0; i < nVehicles * nCalls; i++) {
            loadingTime[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][2];
            unloadingTime[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][4];
            portCost[nodes[i][0] - 1][nodes[i][1] - 1] = nodes[i][3] + nodes[i][5];
        }

        vesselCapacity = new int[nVehicles];
        firstTravelTime = new int[nVehicles][nNodes];
        firstTravelCost = new int[nVehicles][nNodes];

        for (int i = 0; i < nVehicles; i++) {
            vesselCapacity[i] = vehicles.get(i).capacity;
            for (int j = 0; j < nNodes; j++) {
                firstTravelTime[i][j] = travelTime[i][vehicles.get(i).homeNode - 1][j] + vehicles.get(i).startingTime;
                firstTravelCost[i][j] = travelCost[i][vehicles.get(i).homeNode - 1][j];
            }
        }

        for (int call1 = 0; call1 < nCalls; call1++) {

            // Find max and min cargo size
            minCallSize = min(minCallSize, calls.get(call1).size);
            maxCallSize = max(maxCallSize, calls.get(call1).size);
            for (int call2 = call1 + 1; call2 < nCalls; call2++) {

                // Find max and min travel distance
                List<Integer> commonVehicles = new ArrayList<>(calls.get(call1).validVehicles);
                commonVehicles.retainAll(calls.get(call2).validVehicles);
                if (!commonVehicles.isEmpty()) {
                    int finalCall1 = call1;
                    int finalCall2 = call2;
                    double orgToOrg = commonVehicles.stream().reduce(0.0, (acc, vehicle) -> (double) travelTime[vehicle][calls.get(finalCall1).originNode][calls.get(finalCall2).originNode], Double::sum) / commonVehicles.size();
                    double destToDest = commonVehicles.stream().reduce(0.0, (acc, vehicle) -> (double) travelTime[vehicle][calls.get(finalCall1).destinationNode][calls.get(finalCall2).destinationNode], Double::sum) / commonVehicles.size();
                    maxTravelDistance = max(maxTravelDistance, orgToOrg + destToDest);
                    minTravelDistance = min(minTravelDistance, orgToOrg + destToDest);
                }

                // Find max and min pickup and delivery time window
                int PL1 = calls.get(call1).lowerTimePickup;
                int PL2 = calls.get(call2).lowerTimePickup;
                int PU1 = calls.get(call1).upperTimePickup;
                int PU2 = calls.get(call2).upperTimePickup;
                int DL1 = calls.get(call1).lowerTimeDelivery;
                int DL2 = calls.get(call2).lowerTimeDelivery;
                int DU1 = calls.get(call1).upperTimeDelivery;
                int DU2 = calls.get(call2).upperTimeDelivery;

                double pickupDeliveryTimeWindow = min(PU1, PU2) - Math.max(PL1, PL2) + min(DU1, DU2) - Math.max(DL1, DL2);

                maxPickupDeliveryTimeWindow = max(maxPickupDeliveryTimeWindow, pickupDeliveryTimeWindow);
                minPickupDeliveryTimeWindow = min(minPickupDeliveryTimeWindow, pickupDeliveryTimeWindow);
            }
        }
    }

    public Call getCall(int call) {
        return calls.get(call);
    }

    @Override
    public String toString() {
        return "Problem{" +
                "vehicles=" + vehicles +
                ", calls=" + calls +
                '}';
    }
}
