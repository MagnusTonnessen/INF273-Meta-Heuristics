package objects;

public class Call implements Comparable<Call> {

    public final int callIndex;
    public final int originNode;
    public final int destinationNode;
    public final int size;
    public final int costNotTransport;
    public final int lowerTimePickup;
    public final int upperTimePickup;
    public final int lowerTimeDelivery;
    public final int upperTimeDelivery;

    public Call(int[] call) {
        this.callIndex = call[0];
        this.originNode = call[1];
        this.destinationNode = call[2];
        this.size = call[3];
        this.costNotTransport = call[4];
        this.lowerTimePickup = call[5];
        this.upperTimePickup = call[6];
        this.lowerTimeDelivery = call[7];
        this.upperTimeDelivery = call[8];
    }

    @Override
    public int compareTo(Call o) {
        return 0;
    }

    @Override
    public String toString() {
        return "Call{" +
                "callIndex=" + callIndex +
                ", originNode=" + originNode +
                ", destinationNode=" + destinationNode +
                ", size=" + size +
                ", costNotTransport=" + costNotTransport +
                ", lowerTimePickup=" + lowerTimePickup +
                ", upperTimePickup=" + upperTimePickup +
                ", lowerTimeDelivery=" + lowerTimeDelivery +
                ", upperTimeDelivery=" + upperTimeDelivery +
                '}';
    }
}
