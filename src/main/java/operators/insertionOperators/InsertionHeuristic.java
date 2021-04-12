package operators.insertionOperators;

import objects.Solution;

import java.util.List;

public interface InsertionHeuristic {
    Solution insert(Solution solution, List<Integer> calls);
}
