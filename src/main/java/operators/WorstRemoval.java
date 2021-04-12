package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.List;

import static utils.Constants.random;

public class WorstRemoval {

    public List<Integer> operate(Solution solution, int q) {

        List<Vehicle> notEmptyVehicles = solution.getNotEmptyVehicles();

        List<Integer> transportedCalls = solution;
        Vehicle vehicle1 = notEmptyVehicles.get(random.nextInt(notEmptyVehicles.size()));

        notEmptyVehicles.forEach(vehicle -> {
            System.out.println("Vehicle: " + vehicle.currentCalls());
            double c = vehicle.cost();
            vehicle.stream().distinct().forEach(call -> {
                Vehicle v = vehicle.copy();
                System.out.println("Vehicle2: " + v.currentCalls());
                v.removeCall(call);
                double vc = v.cost();
                System.out.println("Vehicle without " + call + ": " + v.cost());
                System.out.println("Call cost: " + (c - vc));
                System.out.println();
            });
        });

        int mostExpensive = vehicle1.stream().distinct().max((call1, call2) -> {
            Vehicle vehicleCopy1 = vehicle1.copy();
            Vehicle vehicleCopy2 = vehicle1.copy();
            vehicleCopy1.removeCall(call1);
            vehicleCopy2.removeCall(call2);
            return -Double.compare(vehicleCopy1.cost(), vehicleCopy2.cost());
        }).get();

        System.out.println(mostExpensive);
        return new ArrayList<>();
    }
}
