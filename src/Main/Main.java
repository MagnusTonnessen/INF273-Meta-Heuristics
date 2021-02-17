package Main;

import Assignments.Assignment2;
import Assignments.Assignment3;

import java.util.Arrays;
import java.util.Map;

import static Utils.PDPUtils.costFunction;
import static Utils.PDPUtils.loadProblem;

public class Main {
    public static void main(String[] args) {
        Assignment3 ass3 = new Assignment3();
        Map<String, Object> problem = loadProblem("resources/Call_7_Vehicle_3.txt");
        int[] initSolution = new Assignment2().generateInitSolution(problem);
        int[] bestSolution = ass3.localSearch(initSolution, problem);
        System.out.println("Best sol: " + Arrays.toString(bestSolution));
        System.out.println("Best cost: " + costFunction(bestSolution, problem));

    }
}
