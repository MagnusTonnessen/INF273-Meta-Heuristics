package objects;

import java.util.HashMap;
import java.util.Map;

public record Results(int[] bestSolution, double bestObjective, double averageObjective, double improvement,
                      double averageRunTime) {

    public Map<String, Object> asMap() {
        return new HashMap<>() {{
            put("Average objective", averageObjective);
            put("Best objective", bestObjective);
            put("Average run time", averageRunTime);
            put("Improvement", improvement);
            put("Best solution", bestSolution);
        }};
    }

    @Override
    public String toString() {
        return "Best objective: " + bestObjective +
                "\nAverage objective: " + averageObjective +
                "\nImprovement: " + improvement +
                "\nAverage run time: " + averageRunTime;
    }
}
