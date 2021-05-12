package objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.Constants.random;
import static utils.Utils.costFunction;
import static utils.Utils.feasibilityCheck;

public class Vehicle extends ArrayList<Integer> {

    public final int vehicleIndex;
    public final int homeNode;
    public final int startingTime;
    public final int capacity;
    public final Set<Integer> validCalls;
    public final boolean isDummy;

    public Vehicle(int[] vehicle, Set<Integer> validCalls, boolean isDummy) {
        this.vehicleIndex = vehicle[0] - 1;
        this.homeNode = vehicle[1];
        this.startingTime = vehicle[2];
        this.capacity = vehicle[3];
        this.validCalls = validCalls;
        this.isDummy = isDummy;
    }

    public static Vehicle dummyVehicle(int nVehicles) {
        return new Vehicle(new int[]{nVehicles + 1, -1, -1, -1}, IntStream.range(0, nVehicles).boxed().collect(Collectors.toSet()), true);
    }

    public void randomInsert(int call) {
        int index1 = random.nextInt(size() + 1);
        int index2 = random.nextInt(size() + 2);
        add(index1, call);
        add(index2, call);
    }

    public void removeCall(int call) {
        removeIf(c -> c == call);
    }

    public void insertCall(int call, int index1, int index2) {
        add(index1, call);
        add(index2, call);
    }

    public int cost() {
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
