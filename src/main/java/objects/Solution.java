package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
import static main.Main.problem;
import static utils.Constants.random;
import static utils.Utils.costFunction;
import static utils.Utils.feasibilityCheck;

public class Solution extends ArrayList<Vehicle> {

    public Solution() {
        addAll(problem.vehicles.stream().sorted(comparingInt(vehicle -> vehicle.vehicleIndex)).collect(Collectors.toList()));
        add(Vehicle.dummyVehicle(problem.nVehicles));
        get(size() - 1).addAll(IntStream.range(0, problem.nCalls).flatMap(i -> IntStream.of(i, i)).boxed().sorted(comparingInt(call -> call)).collect(Collectors.toList()));
    }

    public Solution(Solution solution) {
        super(solution.stream().map(Vehicle::copy).collect(Collectors.toList()));
    }

    public int getVehicleSize(int vehicle) {
        return get(vehicle).size();
    }

    public Vehicle getVehicleFromCall(int call) {
        for (Vehicle vehicle : this) {
            if (vehicle.contains(call)) {
                return vehicle;
            }
        }
        return getDummy();
    }

    public Vehicle getDummy() {
        return get(size() - 1);
    }

    public void removeCall(int call) {
        forEach(vehicle -> vehicle.removeIf(c -> c == call));
    }

    public void removeCalls(List<Integer> calls) {
        calls.forEach(this::removeCall);
    }

    public void moveCall(int call, int toVehicle, int index1, int index2) {
        removeCall(call);
        get(toVehicle).add(index1, call);
        get(toVehicle).add(index2, call);
    }

    public void moveCallRandom(int call, int vehicle) {
        removeCall(call);
        int index1 = random.nextInt(getVehicleSize(vehicle) + 1);
        int index2 = random.nextInt(getVehicleSize(vehicle) + 2);
        moveCall(call, vehicle, index1, index2);
    }

    public Vehicle getVehicle(int call) {
        return stream().filter(vehicle -> vehicle.contains(call)).findFirst().orElse(getDummy());
    }

    public boolean isFeasible() {
        return feasibilityCheck(this);
    }

    public double cost() {
        return costFunction(this);
    }

    public int[] asArray() {
        return stream().flatMapToInt(vehicle -> IntStream.concat(IntStream.of(-1), vehicle.stream().mapToInt(i -> i))).skip(1).toArray();
    }

    public Solution copy() {
        return new Solution(this);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray());
    }
}
