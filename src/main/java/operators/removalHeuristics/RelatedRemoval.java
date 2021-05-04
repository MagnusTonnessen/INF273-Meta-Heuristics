package operators.removalHeuristics;

import objects.Solution;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static main.Main.problem;

public class RelatedRemoval implements RemovalHeuristic {

    private final double[][] relations = getRelations();

    @Override
    public List<Integer> remove(Solution solution, int number) {
        return solution
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list;
                        }))
                .stream()
                .limit(number)
                .collect(Collectors.toList());
    }

    public List<Integer> remove_(Solution solution, int number) {
        return null;
    }

    public double[][] getRelations() {
        return new double[problem.nCalls][problem.nCalls];
    }
}
