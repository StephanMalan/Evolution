package main;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private List<List<Neuron>> nodes;

    public NeuralNetwork(int numInputs, int numOutputs) {
        init(numInputs, numOutputs);
        nodes = new ArrayList<>();
    }

    private void init(int numInputs, int numOutputs) {
        int numNeuronRows = (int) ((Math.random() * (Constants.MAX_NUM_NEURON_ROWS - Constants.MIN_NUM_NEURON_ROWS + 1)) + Constants.MIN_NUM_NEURON_ROWS);
        for (int nr = 0; nr < numNeuronRows; nr++) {
            if (nr == 0) {
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int in = 0; in < numInputs; in++) {
                    tempNeurons.add(new Neuron());
                }
                nodes.add(tempNeurons);
            } else if (nr == numNeuronRows - 1) {
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int out = 0; out < numOutputs; out++) {
                    tempNeurons.add(new Neuron());
                }
                nodes.add(tempNeurons);
            } else {
                int numNeurons = (int) ((Math.random() * (Constants.MAX_NUM_NEURON - Constants.MIN_NUM_NEURON + 1)) + Constants.MIN_NUM_NEURON);
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int hid = 0; hid < numNeurons; hid++) {
                    tempNeurons.add(new Neuron());
                }
                nodes.add(tempNeurons);
            }
        }
    }

    private double sigmoid(double input) {
        return 1 / (1 + Math.pow(Math.E, -input));
    }

    public static void main(String[] args) {
        new NeuralNetwork(5, 3);
    }

}
