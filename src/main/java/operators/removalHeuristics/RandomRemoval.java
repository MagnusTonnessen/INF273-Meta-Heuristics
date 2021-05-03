package operators.removalHeuristics;

import objects.Solution;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RandomRemoval implements RemovalHeuristic {
    @Override
    public List<Integer> remove(Solution solution, int number) {
        return solution
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                /*
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list;
                        }))
                .stream()
                */
                .limit(number)
                .collect(Collectors.toList());
    }
}
