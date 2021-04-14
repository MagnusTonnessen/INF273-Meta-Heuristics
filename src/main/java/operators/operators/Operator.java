package operators.operators;

import objects.Solution;
import operators.insertionHeuristics.GreedyInsertion;
import operators.insertionHeuristics.InsertionHeuristic;
import operators.insertionHeuristics.RegretKInsertion;
import operators.removalHeuristics.RandomRemoval;
import operators.removalHeuristics.RelatedRemoval;
import operators.removalHeuristics.RemovalHeuristic;
import operators.removalHeuristics.WorstRemoval;

public abstract class Operator {

    protected static final InsertionHeuristic greedyInsertion = new GreedyInsertion();
    protected static final InsertionHeuristic regretKInsertion = new RegretKInsertion();

    protected static final RemovalHeuristic randomRemoval = new RandomRemoval();
    protected static final RemovalHeuristic worstRemoval = new WorstRemoval();
    protected static final RemovalHeuristic relatedRemoval = new RelatedRemoval();

    /**
     * @param solution initial solution to operate on
     * @return new solution after executing an operation
     */
    public Solution operate(Solution solution) {
        return new Solution();
    }
}
