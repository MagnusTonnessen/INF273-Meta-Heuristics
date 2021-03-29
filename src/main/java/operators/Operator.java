package operators;

import objects.Solution;

public interface Operator {

    /**
     * @param solution initial solution to operate on
     * @return new solution after executing en operation
     */
    Solution operate(Solution solution);
}
