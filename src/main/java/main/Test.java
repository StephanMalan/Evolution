package main;

import java.util.Arrays;

public class Test {

    public Test() {
        getCoords();
    }

    private void getCoords() {
        for (int i = 0; i < 10; i++) {
            int x = 450;
            int y = 450;
            while (Math.hypot(450 - x, 450 - y) <= 200) {
                x = (int) ((Math.random() * (601)) + 100);
                y = (int) ((Math.random() * (601)) + 100);
            }
            System.out.println(x + ":" + y);
        }
    }

    private void calibrateSigmoid() {
        double avg = 0;
        for (int count = 0; count < 100; count++) {
            NeuralNetwork neuralNetwork = new NeuralNetwork(15, 15);
            double[] inputs = new double[15];
            double highestDifference = 0;
            double hda = 0;
            for (double i = 0.01; i < 5.0; i += 0.01) {
                double[] prevOutputs = new double[15];
                double difference = 0;
                for (double ss = 0.0; ss <= 1.0; ss += 0.01) {
                    Arrays.fill(inputs, ss);
                    double[] outputs = neuralNetwork.getOutputs(inputs, i);
                    if (ss != 0.0) {
                        for (int d = 0; d < inputs.length; d++) {
                            difference += Math.max(outputs[d], prevOutputs[d]) - Math.min(outputs[d], prevOutputs[d]);
                        }
                    }
                    prevOutputs = inputs.clone();
                }
                if (difference > highestDifference) {
                    highestDifference = difference;
                    hda = i;
                }
            }
            avg += hda;
            System.out.println(hda + " : " + highestDifference);
        }
        System.out.println("Complete");
        System.out.println("Calibrated: " + (avg / 100));
    }

    public static void main(String[] args) {
        new Test();
    }

}
