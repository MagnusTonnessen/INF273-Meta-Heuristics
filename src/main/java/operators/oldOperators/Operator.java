package operators.oldOperators;

import objects.Solution;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Operator {

    /**
     * @param solution initial solution to operate on
     * @return new solution after executing an operation
     */
    public abstract Solution operate(Solution solution);

    public String getName() {
        return Arrays
                .stream(getClass().getSimpleName().split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
