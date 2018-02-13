package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork implements Serializable {

    private List<List<Neuron>> neurons;

    public NeuralNetwork(int numInputs, int numOutputs) {
        neurons = new ArrayList<>();
        init(numInputs, numOutputs);
    }

    private void init(int numInputs, int numOutputs) {
        int numNeuronRows = (int) ((Math.random() * (Constants.MAX_NUM_NEURON_ROWS - Constants.MIN_NUM_NEURON_ROWS + 1)) + Constants.MIN_NUM_NEURON_ROWS);
        for (int nr = 0; nr < numNeuronRows; nr++) {
            if (nr == 0) {
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int in = 0; in < numInputs; in++) {
                    tempNeurons.add(new Neuron());
                }
                neurons.add(tempNeurons);
            } else if (nr == numNeuronRows - 1) {
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int out = 0; out < numOutputs; out++) {
                    tempNeurons.add(new Neuron(neurons.get(neurons.size() - 1).size()));
                }
                neurons.add(tempNeurons);
            } else {
                int numNeurons = (int) ((Math.random() * (Constants.MAX_NUM_NEURON - Constants.MIN_NUM_NEURON + 1)) + Constants.MIN_NUM_NEURON);
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int hid = 0; hid < numNeurons; hid++) {
                    tempNeurons.add(new Neuron(neurons.get(neurons.size() - 1).size()));
                }
                neurons.add(tempNeurons);
            }
        }
    }

    public double[] getOutputs(double[] inputs, double a) {
        for (int nr = 0; nr < neurons.size(); nr++) {
            if (nr == 0) {
                for (int n = 0; n < neurons.get(0).size(); n++) {
                    neurons.get(0).get(n).setValue(inputs[n]);
                }
            } else {
                for (int n = 0; n < neurons.get(nr).size(); n++) {
                    Neuron neuron = neurons.get(nr).get(n);
                    double[] weights = neuron.getWeights();
                    double total = neuron.getBias();
                    for (int w = 0; w < weights.length; w++) {
                        total += weights[w] * neurons.get(nr - 1).get(w).getValue();
                    }
                    neuron.setValue(Constants.sigmoid(total, a));
                }
            }
        }
        double[] outputs = new double[neurons.get(neurons.size() - 1).size()];
        for (int n = 0; n < outputs.length; n++) {
            outputs[n] = neurons.get(neurons.size() - 1).get(n).getValue();
        }
        return outputs;
    }

    public int getNeuronCount() {
        int neuronCount = 0;
        for (List<Neuron> neuronsRows : neurons) {
            neuronCount += neuronsRows.size();
        }
        return neuronCount;
    }

    public void draw(GraphicsContext gc) {
        int neuronRowCount = neurons.size();
        int maxNeuronsInRow = getMaxNeuronsInRow();
        int neuronDiameter = (int) Math.min(Constants.NEURON_CANVAS_SIZE / maxNeuronsInRow / 3 * 2, Constants.NEURON_CANVAS_SIZE / neuronRowCount / 3 * 2);
        int xOffset = (int) ((Constants.NEURON_CANVAS_SIZE - (neurons.size() * neuronDiameter)) / (neurons.size() + 1));
        for (int nr = 0; nr < neurons.size(); nr++) {
            int x = xOffset + (nr * (xOffset + neuronDiameter));
            int yOffset = (int) ((Constants.NEURON_CANVAS_SIZE - (neurons.get(nr).size() * neuronDiameter)) / (neurons.get(nr).size() + 1));
            for (int n = 0; n < neurons.get(nr).size(); n++) {
                int y = yOffset + (n * (yOffset + neuronDiameter));
                Color neuronColor = new Color(Math.min(1, Math.max(0, 1 - neurons.get(nr).get(n).getValue())), Math.min(1, Math.max(0, neurons.get(nr).get(n).getValue())), 0, 1);
                gc.setFill(neuronColor);
                gc.fillOval(x, y, neuronDiameter, neuronDiameter);
            }
        }
    }

    private int getMaxNeuronsInRow() {
        int out = 0;
        for (List<Neuron> neuronRow : neurons) {
            if (neuronRow.size() > out) {
                out = neuronRow.size();
            }
        }
        return out;
    }

    public static void main(String[] args) {
        new NeuralNetwork(5, 3);
    }

    public void mutate(int numInputs, int numOutputs) {
        int numNeuronRows = (int) (Math.max((Math.random() * 3) + Constants.MIN_NUM_NEURON_ROWS - 1, Constants.MIN_NUM_NEURON_ROWS));
        if (numNeuronRows > neurons.size()) {
            for (int add = 0; add < numNeuronRows - neurons.size(); add++) {
                int randomIndex = (int) ((Math.random() * (neurons.size() - 1)) + 1);
                int numNeurons = (int) ((Math.random() * (Constants.MAX_NUM_NEURON - Constants.MIN_NUM_NEURON + 1)) + Constants.MIN_NUM_NEURON);
                List<Neuron> tempNeurons = new ArrayList<>();
                for (int hid = 0; hid < numNeurons; hid++) {
                    tempNeurons.add(new Neuron(neurons.get(neurons.size() - 1).size()));
                }
                neurons.add(randomIndex, tempNeurons);
            }
        } else if (numNeuronRows < neurons.size()) {
            for (int remove = 0; remove < neurons.size() - numNeuronRows; remove++) {
                int randomIndex = (int) ((Math.random() * (neurons.size() - 2)) + 1);
                neurons.remove(randomIndex);
            }
        }
        if (neurons.get(0).size() > numInputs) {
            for (int remove = 0; remove < neurons.get(0).size() - numInputs; remove++) {
                neurons.get(0).remove(neurons.get(0).size() - 1);
            }
        } else if (neurons.get(0).size() < numInputs) {
            for (int add = 0; add < numInputs - neurons.get(0).size(); add++) {
                neurons.get(0).add(new Neuron());
            }
        }
        if (neurons.get(neurons.size() - 1).size() > numOutputs) {
            for (int remove = 0; remove < neurons.get(neurons.size() - 1).size() - numOutputs; remove++) {
                neurons.get(neurons.size() - 1).remove(neurons.get(neurons.size() - 1).size() - 1);
            }
        } else if (neurons.get(neurons.size() - 1).size() < numOutputs) {
            for (int add = 0; add < numOutputs - neurons.get(neurons.size() - 1).size(); add++) {
                neurons.get(neurons.size() - 1).add(new Neuron());
            }
        }

        for (int nr = 0; nr < neurons.size(); nr++) {
            for (int n = 0; n < neurons.get(nr).size(); n++) {
                if (nr == 0) {
                    neurons.get(nr).get(n).mutate();
                } else {
                    neurons.get(nr).get(n).mutate(neurons.get(nr - 1).size());
                }
            }
        }

    }

    public double getWeight() {
        return getNeuronCount() * Constants.NEURON_WEIGHT;
    }
}
