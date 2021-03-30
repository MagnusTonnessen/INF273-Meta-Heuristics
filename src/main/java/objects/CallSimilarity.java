package objects;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static main.Main.problem;

public class CallSimilarity {

    public static Map<Integer, Map<Integer, Double>> calculateCallSimilarities() {
        Map<Integer, Map<Integer, Double>> similarities = IntStream.range(0, problem.calls.size()).boxed().collect(toMap(i -> i, i -> new HashMap<>()));
        for (int call1 = 0; call1 < problem.calls.size() - 1; call1++) {
            for (int call2 = call1 + 1; call2 < problem.nCalls; call2++) {
                double similarity = similarity(call1, call2);
                similarities.get(call1).put(call2, similarity);
                similarities.get(call2).put(call1, similarity);
            }
        }
        return similarities;
    }

    private static double similarity(int call1, int call2) {
        return 0;
    }

    private static double distanceSimilarity(int call1, int call2) {
        int orig1 = problem.calls.get(call1).originNode;
        int orig2 = problem.calls.get(call2).originNode;

        double minOrigDist = IntStream
                .range(0, problem.nVehicles)
                .mapToDouble(vehicle -> problem.travel.get(vehicle).get(orig1).get(orig2).travelTime)
                .min()
                .orElse(0);

        int dest1 = problem.calls.get(call1).destinationNode;
        int dest2 = problem.calls.get(call2).destinationNode;

        double minDestDist = IntStream
                .range(0, problem.nVehicles)
                .mapToDouble(vehicle -> problem.travel.get(vehicle).get(orig1).get(orig2).travelTime)
                .min()
                .orElse(0);
        return normalize((minOrigDist + minDestDist), 100, 0);
    }

    private static double normalize(double num, double max, double min) {
        return max == min ? 0.5 : (num - min)/(max - min);
    }
}
