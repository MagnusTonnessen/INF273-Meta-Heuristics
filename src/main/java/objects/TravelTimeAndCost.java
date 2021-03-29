package objects;

public class TravelTimeAndCost {

    public final int vehicle;
    public final int originNode;
    public final int destinationNode;
    public final int travelTime;
    public final int travelCost;

    public TravelTimeAndCost(int[] travel) {
        this.vehicle = travel[0] - 1;
        this.originNode = travel[1];
        this.destinationNode = travel[2];
        this.travelTime = travel[3];
        this.travelCost = travel[4];
    }

    @Override
    public String toString() {
        return "TravelTimeAndCost{" +
                "vehicle=" + vehicle +
                ", originNode=" + originNode +
                ", destinationNode=" + destinationNode +
                ", travelTime=" + travelTime +
                ", travelCost=" + travelCost +
                '}';
    }
}
