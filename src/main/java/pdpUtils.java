import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class pdpUtils {

    public Map<String, Object> loadProblem(String filename) {

        Map<String, Object> problemMap = new HashMap<>();

        try {
            int nNodes;
            int nVehicles;
            int nCalls;

            List<String[]> A = new ArrayList<>();
            List<String[]> B = new ArrayList<>();
            int[][] D;
            List<String[]> E = new ArrayList<>();

            double[][] cargo;
            double[][][] travelTime;
            double[][][] travelCost;
            double[][][] travelTime1;
            double[][][] travelCost1;
            double[] firstTravelTime;
            double[] vesselCapacity;
            double[] loadingTime;
            double[] unloadingTime;
            double[] vesselCargo;
            double[] firstTravelCost;
            double[] portCost;

            List<String> input = Files.readAllLines(Path.of(filename));

            nNodes = Integer.parseInt(input.get(1));
            nVehicles = Integer.parseInt(input.get(3));
            nCalls = Integer.parseInt(input.get(nVehicles + 6));

            for (int i = 0; i < nVehicles; i++) {
                A.add(input.get(1 + 4 + i).split(","));
            }

            for (int i = 0; i < nVehicles; i++) {
                B.add(input.get(1 + 7 + nVehicles + i).split(","));
            }

            cargo = IntStream
                    .range(0, nCalls)
                    .mapToObj(i -> Arrays
                            .stream(input.get(1 + 8 + nVehicles * 2 + i).split(","))
                            .skip(1)
                            .mapToDouble(Double::parseDouble)
                            .toArray())
                    .toArray(double[][]::new);

            travelCost = new double[nVehicles + 1][nNodes + 1][nNodes + 1];
            travelTime = new double[nVehicles + 1][nNodes + 1][nNodes + 1];

            IntStream
                    .range(0, nNodes * nNodes * nVehicles)
                    .mapToObj(i -> Arrays
                            .stream(input.get(1 + 2 * nVehicles + nCalls + 9 + i).split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .forEach(arr -> {
                        travelTime[arr[0]][arr[1]][arr[2]] = arr[3];
                        travelCost[arr[0]][arr[1]][arr[2]] = arr[4];
                    });

            for (int i = 0; i < nVehicles; i++) {
                E.add(input.get(1 + 1 + 2 * nVehicles + nCalls + 10 + nNodes * nNodes * nVehicles - 1 + i).split(","));
            }

            vesselCapacity = new double[nVehicles];
            double[] startingTime = new double[nVehicles];



        } catch (Exception e) {
            e.printStackTrace();
        }
        return problemMap;
    }


}
