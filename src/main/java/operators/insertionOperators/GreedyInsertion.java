package operators.insertionOperators;

import objects.Solution;
import objects.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GreedyInsertion implements InsertionHeuristic {
    @Override
    public Solution insert(Solution solution, List<Integer> calls) {
        List<double[]> best =  calls.stream().map(call -> bestInsert(solution, call)).collect(Collectors.toList());
        for (int i = 0; i < calls.size(); i++) {
            best.stream().min(Comparator.comparingDouble(call -> call[4])).ifPresent(call -> {
                solution.moveCall((int) call[0], (int) call[1], (int) call[2], (int) call[3]);
                best.removeIf(c -> c[1] == call[1]);
                IntStream.range(0, best.size()).filter(j -> best.get(j)[1] == call[1]).forEach(j -> best.set(j, bestInsert(solution, (int) best.get(j)[0])));
            });
        }
        return solution;
    }

    private double[] bestInsert(Solution solution, int call) {
        double minCost = Integer.MAX_VALUE;
        Vehicle cheapestInsert = solution.getDummy();
        int insert1 = 0;
        int insert2 = 1;
        for (Vehicle vehicle : solution) {
            if (!vehicle.isDummy) {
                for (int i = 0; i < vehicle.size() + 1; i++) {
                    for (int j = i+1; j < vehicle.size() + 2; j++) {
                        Vehicle copy = vehicle.copy();
                        copy.add(i, call);
                        copy.add(j, call);
                        double cost = copy.cost();
                        if (cost < minCost && copy.isFeasible()) {
                            minCost = cost;
                            cheapestInsert = copy;
                            insert1 = i;
                            insert2 = j;
                        }
                    }
                }
            }
        }
        return new double[] {call, cheapestInsert.vehicleIndex, insert1, insert2, minCost};
    }
}
