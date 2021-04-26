package operators.removalHeuristics;

import objects.Solution;
import objects.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.Main.problem;

public class RelatedRemoval implements RemovalHeuristic {
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
}
