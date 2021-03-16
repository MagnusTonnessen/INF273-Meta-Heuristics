package objects;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.generateInitSolution;

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
    public final int[] initialSolution;
    public final double initialCost;
    public final Vehicle[] vehicles;
    public final Call[] calls;
    public final Map<Integer, Call> callsMap;
    public final TravelTimeAndCost[] travelTimeAndCosts;
    public final NodeTimeAndCost[] nodeTimeAndCosts;

    public Problem(int nCalls, int nVehicles, int nNodes, int[] vesselCapacity, int[][] cargo, int[][] firstTravelTime, int[][] firstTravelCost, int[][] loadingTime, int[][] unloadingTime, int[][] vesselCargo, int[][] portCost, int[][][] travelTime, int[][][] travelCost, Vehicle[] vehicles, Call[] calls, TravelTimeAndCost[] travelTimeAndCosts, NodeTimeAndCost[] nodeTimeAndCosts) {
        this.nCalls = nCalls;
        this.nVehicles = nVehicles;
        this.nNodes = nNodes;
        this.vesselCapacity = vesselCapacity;
        this.cargo = cargo;
        this.firstTravelTime = firstTravelTime;
        this.firstTravelCost = firstTravelCost;
        this.loadingTime = loadingTime;
        this.unloadingTime = unloadingTime;
        this.vesselCargo = vesselCargo;
        this.portCost = portCost;
        this.travelTime = travelTime;
        this.travelCost = travelCost;
        this.vehicles = vehicles;
        this.calls = calls;
        this.travelTimeAndCosts = travelTimeAndCosts;
        this.nodeTimeAndCosts = nodeTimeAndCosts;
        this.callsMap = Arrays.stream(calls).collect(Collectors.toMap(c -> c.callIndex, c -> c));
        this.initialSolution = generateInitSolution(nCalls, nVehicles);
        this.initialCost = costFunction(initialSolution, nVehicles, cargo, firstTravelCost, portCost, travelCost);
    }
}
