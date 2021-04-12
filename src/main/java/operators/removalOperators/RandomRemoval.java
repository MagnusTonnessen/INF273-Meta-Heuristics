package operators.removalOperators;

import objects.Solution;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomRemoval implements RemovalHeuristic {
    @Override
    public List<Integer> remove(Solution solution, int number) {
        return solution
                .getTransportedCalls()
                .stream()
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
