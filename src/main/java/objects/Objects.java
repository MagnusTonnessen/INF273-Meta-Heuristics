package objects;

public class Objects {

    public static final record Vehicle(int vehicleIndex, int homeNode, int startingTime, int capacity, int[] validCalls) {}

    public static final record Call(int callIndex, int originNode, int destinationNode, int size, int costNotTransport, int lowerTimePickup, int upperTimePickup, int lowerTimeDelivery, int upperTimeDelivery) {}

    public static final record TravelTimeAndCost(int vehicle, int originNode, int destinationNode, int travelTime, int travelCost) {}

    public static final record NodeTimeAndCost(int vehicle, int call, int originNodeTime, int originNodeCost, int destinationNodeTime, int destinationNodeCosts) {}

}
