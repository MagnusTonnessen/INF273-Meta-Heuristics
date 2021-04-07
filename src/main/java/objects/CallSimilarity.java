package objects;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static main.Main.problem;

public class CallSimilarity {

    public Map<Integer, Map<Integer, Double>> calculateCallSimilarities() {
        Map<Integer, Map<Integer, Double>> similarities = IntStream.range(0, problem.calls.size()).boxed().collect(toMap(i -> i, i -> new HashMap<>()));
        for (int call1 = 0; call1 < problem.calls.size() - 1; call1++) {
            for (int call2 = call1 + 1; call2 < problem.nCalls; call2++) {
                double similarity = getSimilarity(call1, call2);
                similarities.get(call1).put(call2, similarity);
                similarities.get(call2).put(call1, similarity);
            }
        }
        return similarities;
    }

    public double getSimilarity(int call1, int call2) {
        return 0.0;
    }

    public double getCallDistance(int call1, int call2) {
        return 0.0;
    }
}
