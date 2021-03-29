package algorithms;

import objects.Solution;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface SearchingAlgorithm {

    Solution search();

    default String getName() {
        return Arrays
                .stream(getClass().getSimpleName().split("(?=[A-Z])"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
