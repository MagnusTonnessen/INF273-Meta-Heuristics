package operators;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import static utils.Constants.random;

public class WorstRemoval implements Operator {
    @Override
    public Solution operate(Solution solution) {

        List<Vehicle> notEmptyVehicles = solution.getNotEmptyVehicles();

        Vehicle vehicle1 = notEmptyVehicles.get(random.nextInt(notEmptyVehicles.size()));

        notEmptyVehicles.stream().forEach(vehicle -> {
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
        return solution;
    }
}
