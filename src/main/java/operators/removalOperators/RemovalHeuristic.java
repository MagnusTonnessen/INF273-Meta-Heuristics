package operators.removalOperators;

import objects.Solution;

import java.util.List;

public interface RemovalHeuristic {
    List<Integer> remove(Solution solution, int number);
}
