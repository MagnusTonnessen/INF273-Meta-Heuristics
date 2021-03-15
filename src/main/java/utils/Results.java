package utils;

import java.util.HashMap;
import java.util.Map;

public class Results {
    /*
    resultsMap.put("Best solution", bestSolution);
        resultsMap.put("Best objective", bestCost);
        resultsMap.put("Average objective", averageCost);
        resultsMap.put("Improvement", improvement);
        resultsMap.put("Average run time", averageExecutionTime);
     */

    public final int[] bestSolution;
    public final double bestObjective;
    public final double averageObjective;
    public final double improvement;
    public final double averageRunTime;

    public Results(int[] bestSolution, double bestObjective, double averageObjective, double improvement, double averageRunTime) {
        this.bestSolution = bestSolution;
        this.bestObjective = bestObjective;
        this.averageObjective = averageObjective;
        this.improvement = improvement;
        this.averageRunTime = averageRunTime;
    }

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
