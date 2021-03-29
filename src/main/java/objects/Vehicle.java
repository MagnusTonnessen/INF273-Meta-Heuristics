package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.Utils.costFunction;
import static utils.Utils.feasibilityCheck;

public class Vehicle extends ArrayList<Integer> {

    public final int vehicleIndex;
    public final int homeNode;
    public final int startingTime;
    public final int capacity;
    public final Set<Integer> validCalls;

    public Vehicle(int[] vehicle, Set<Integer> validCalls) {
        this.vehicleIndex = vehicle[0] - 1;
        this.homeNode = vehicle[1];
        this.startingTime = vehicle[2];
        this.capacity = vehicle[3];
        this.validCalls = validCalls;
    }

    public static Vehicle dummyVehicle(int nCalls) {
        return new Vehicle(new int[]{0, -1, -1, -1}, IntStream.range(0, nCalls).boxed().collect(Collectors.toSet()));
    }

    public void removeCall(int call) {
        removeIf(c -> c == call);
    }

    public double cost() {
        return costFunction(this);
    }

    public boolean isFeasible() {
        return feasibilityCheck(this);
    }

    public int[] asArray() {
        return stream().mapToInt(c -> c).toArray();
    }

    public Vehicle copy() {
        return (Vehicle) clone();
    }

    public List<Integer> indexes(int call) {
        return IntStream.range(0, size()).filter(c -> get(c) == call).boxed().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleIndex=" + vehicleIndex +
                ", homeNode=" + homeNode +
                ", startingTime=" + startingTime +
                ", capacity=" + capacity +
                ", validCalls=" + validCalls +
                ", currentCalls=" + super.toString() +
                '}';
    }
}
