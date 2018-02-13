package main;

import java.io.Serializable;
import java.util.Arrays;

public class Neuron implements Serializable {

    private double[] weights;
    private double bias;
    private double value;

    public Neuron() {
        weights = new double[]{};
        bias = Math.random() * Constants.MAX_NUM_WEIGHT;
        value = 0;
    }

    public Neuron(int numPrevNeurons) {
        weights = new double[numPrevNeurons];
        for (int i = 0; i < numPrevNeurons; i++) {
            weights[i] = (Math.random() * (Constants.MAX_NUM_WEIGHT - Constants.MIN_NUM_WEIGHT)) + Constants.MIN_NUM_WEIGHT;
        }
        bias = Math.random() * Constants.MAX_NUM_WEIGHT;
        value = 0;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getBias() {
        return bias;
    }

    public double getValue() {
        return value;
    }

    public double[] getWeights() {
        return weights;
    }

    public void mutate() {
        bias += (Math.random() * 0.2) - 0.1;
    }

    public void mutate(int numWeights) {
        bias += (Math.random() * 0.2) - 0.1;
        if (weights.length > numWeights) {
            double[] tempArray = weights.clone();
            weights = Arrays.copyOfRange(tempArray, 0, numWeights);
        } else if (weights.length < numWeights) {
            double[] tempArray = weights.clone();
            weights = new double[numWeights];
            for (int i = 0; i < weights.length; i++) {
                if (i < tempArray.length) {
                    weights[i] = tempArray[i];
                } else {
                    weights[i] = (Math.random() * (Constants.MAX_NUM_WEIGHT - Constants.MIN_NUM_WEIGHT)) + Constants.MIN_NUM_WEIGHT;
                }
            }
        }
        for (int i = 0; i < numWeights; i++) {
            weights[i] += (Math.random() * 0.2) - 0.1;
        }
    }
}
