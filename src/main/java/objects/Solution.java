package objects;

import operators.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
import static utils.Constants.random;
import static utils.PDPUtils.costFunction;
import static utils.PDPUtils.feasibilityCheck;

public class Solution extends ArrayList<Vehicle> {

    public Solution(int nCalls, List<Vehicle> vehicles) {
        setInitialSolution(nCalls, vehicles);
    }

    public void setInitialSolution(int nCalls, List<Vehicle> vehicles) {
        addAll(vehicles.stream().sorted(comparingInt(vehicle -> vehicle.vehicleIndex)).toList());
        add(Vehicle.dummyVehicle(nCalls));
        get(size() - 1).addAll(IntStream.range(0, nCalls).flatMap(i -> IntStream.of(i, i)).boxed().sorted(comparingInt(call -> call)).toList());
    }

    /*
    public List<List<Integer>> splitVehicles() {
        modified = false;
        List<Integer> zeroIndexes = IntStream.range(0, size()).filter(i -> get(i) == 0).boxed().toList();
        return IntStream
                .rangeClosed(0, problem.nVehicles)
                .mapToObj(i -> subList(i == 0 ? 0 : zeroIndexes.get(i-1)+1, i == problem.nVehicles ? size() : zeroIndexes.get(i)))
                .toList();
    }
     */
    public int getVehicleSize(int vehicle) {
        return get(vehicle).size();
    }

    public Vehicle getVehicle(int vehicle) {
        return get(vehicle);
    }

    public void removePickupCall(int call) {
        forEach(vehicle -> vehicle.remove(call));
    }

    public void removeDeliveryCall(int call) {
        forEach(vehicle -> vehicle.remove(lastIndexOf(call)));
    }

    public void removeCall(int call) {
        forEach(vehicle -> vehicle.removeIf(c -> c == call));
    }

    public void moveCall(int call, int toVehicle, int index1, int index2) {
        removeCall(call);
        getVehicle(toVehicle).add(index1, call);
        getVehicle(toVehicle).add(index2, call);
    }

    public void moveCalls(int call, int vehicle) {
        int index1 = random.nextInt(getVehicleSize(vehicle) + 2);
        int index2 = random.nextInt(getVehicleSize(vehicle) + 2);
        moveCall(call, vehicle, index1, index2);
    }

    public Solution applyOperator(Operator operator) {
        return operator.operate(this);
    }

    public List<Vehicle> getVehiclesWithNToMCalls(int N, int M) {
        return stream().filter(vehicle -> N <= vehicle.size() && vehicle.size() <= M).toList();
    }

    public List<Vehicle> copy() {
        return (List<Vehicle>) this.clone();
    }

    public boolean isFeasible() {
        return feasibilityCheck(this);
    }

    public double cost() {
        return costFunction(this);
    }

    public int[] asArray() {
        return stream().flatMapToInt(vehicle -> IntStream.concat(vehicle.stream().mapToInt(i -> i), IntStream.of(0))).toArray();
    }
}
