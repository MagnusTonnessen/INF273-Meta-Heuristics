package objects;

import java.util.Arrays;

public class Vehicle {

    public final int vehicleIndex;
    public final int homeNode;
    public final int startingTime;
    public final int capacity;
    public final int[] validCalls;

    public Vehicle(int[] vehicle, int[] validCalls) {
        this.vehicleIndex = vehicle[0];
        this.homeNode = vehicle[1];
        this.startingTime = vehicle[2];
        this.capacity = vehicle[3];
        this.validCalls = validCalls;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleIndex=" + vehicleIndex +
                ", homeNode=" + homeNode +
                ", startingTime=" + startingTime +
                ", capacity=" + capacity +
                ", validCalls=" + Arrays.toString(validCalls) +
                '}';
    }
}
