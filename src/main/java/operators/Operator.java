package operators;

import objects.Solution;

public abstract class Operator {

    private double probability = 0;
    private double cumulativeProbability = 0;

    /**
     * @param solution initial solution to operate on
     * @return new solution after executing en operation
     */
    public Solution operate(Solution solution) { return new Solution(); };

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getCumulativeProbability() {
        return cumulativeProbability;
    }

    public void setCumulativeProbability(double cumulativeProbability) {
        this.cumulativeProbability = cumulativeProbability;
    }
}
