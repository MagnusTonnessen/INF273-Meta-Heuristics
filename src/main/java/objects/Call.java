package objects;

import java.util.Objects;

public class Call {

    public final int callIndex;
    public final int originNode;
    public final int destinationNode;
    public final int size;
    public final int costNotTransport;
    public final int lowerTimePickup;
    public final int upperTimePickup;
    public final int lowerTimeDelivery;
    public final int upperTimeDelivery;
    public final int[] validVehicles;

    public Call(int[] call, int[] validVehicles) {
        this.callIndex = call[0] - 1;
        this.originNode = call[1] - 1;
        this.destinationNode = call[2] - 1;
        this.size = call[3];
        this.costNotTransport = call[4];
        this.lowerTimePickup = call[5];
        this.upperTimePickup = call[6];
        this.lowerTimeDelivery = call[7];
        this.upperTimeDelivery = call[8];
        this.validVehicles = validVehicles;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return callIndex == call.callIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(callIndex);
    }
}
