package operators;

import objects.Solution;

public abstract class Operator {
    /**
     * @param solution initial solution to operate on
     * @return new solution after executing an operation
     */
    public Solution operate(Solution solution) {
        return new Solution();
    }
}
