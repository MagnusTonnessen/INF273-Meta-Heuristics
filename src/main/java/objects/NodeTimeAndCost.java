package objects;

public class NodeTimeAndCost {

    public final int vehicle;
    public final int call;
    public final int originNodeTime;
    public final int originNodeCost;
    public final int destinationNodeTime;
    public final int destinationNodeCosts;

    public NodeTimeAndCost(int[] node) {
        this.vehicle = node[0];
        this.call = node[1];
        this.originNodeTime = node[2];
        this.originNodeCost = node[3];
        this.destinationNodeTime = node[4];
        this.destinationNodeCosts = node[5];
    }

    @Override
    public String toString() {
        return "NodeTimeAndCost{" +
                "vehicle=" + vehicle +
                ", call=" + call +
                ", originNodeTime=" + originNodeTime +
                ", originNodeCost=" + originNodeCost +
                ", destinationNodeTime=" + destinationNodeTime +
                ", destinationNodeCosts=" + destinationNodeCosts +
                '}';
    }
}
