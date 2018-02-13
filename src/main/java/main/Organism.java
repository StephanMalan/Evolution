package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Organism implements Serializable {

    private int ID;
    private boolean alive;
    private double x;
    private double y;
    private double rotation;
    private double weight;
    private double foodCount;
    private int generation;
    private List<Output> outputs;
    private List<Input> inputs;
    private NeuralNetwork neuralNetwork;

    public Organism(int ID) {
        this.ID = ID;
        alive = true;
        x = Constants.CANVAS_SIZE / 2;
        y = Constants.CANVAS_SIZE / 2;
        rotation = 0;
        weight = 0;
        foodCount = Constants.DEFAULT_FOOD_COUNT;
        outputs = new ArrayList<>();
        inputs = new ArrayList<>();
    }

    public void generateNew() {

        generation = 1;

        int numOutputs = (int) ((Math.random() * (Constants.MAX_NUMBER_MUSCLES - Constants.MIN_NUMBER_MUSCLES + 1)) + Constants.MIN_NUMBER_MUSCLES);
        boolean muscleMade = false;
        boolean rotateMuscleMade = false;
        for (int i = 0; i < numOutputs; i++) {
            if (i == numOutputs - 1 && !muscleMade) {
                Muscle muscle = new Muscle();
                weight += muscle.getWeight();
                outputs.add(muscle);
            } else if (i == numOutputs - 1 && !rotateMuscleMade) {
                RotateMuscle rotateMuscle = new RotateMuscle();
                weight += rotateMuscle.getWeight();
                outputs.add(rotateMuscle);
            } else if (Math.random() < 0.5) {
                muscleMade = true;
                Muscle muscle = new Muscle();
                weight += muscle.getWeight();
                outputs.add(muscle);
            } else {
                rotateMuscleMade = true;
                RotateMuscle rotateMuscle = new RotateMuscle();
                weight += rotateMuscle.getWeight();
                outputs.add(rotateMuscle);
            }
        }

        int numInputs = (int) ((Math.random() * (Constants.MAX_NUMBER_SENSORS - Constants.MIN_NUMBER_SENSORS + 1)) + Constants.MIN_NUMBER_SENSORS);
        for (int i = 0; i < numInputs; i++) {
            if (Math.random() < 0.5) {
                Sensor sensor = new Sensor();
                weight += sensor.getWeight();
                inputs.add(sensor);
            } else {
                ProximitySensor proximitySensor = new ProximitySensor();
                weight += proximitySensor.getWeight();
                inputs.add(proximitySensor);
            }
        }

        neuralNetwork = new NeuralNetwork(inputs.size(), outputs.size());
        weight += neuralNetwork.getWeight();
    }

    public void generateNew(Organism o, int newGeneration) {

        Organism og = Constants.copyOrganism(o);

        generation = newGeneration;

        List<Output> ogOutputs = og.outputs;
        List<Input> ogInputs = og.inputs;

        int numOutputs = (int) (Math.max((Math.random() * 3) + ogOutputs.size() - 1, Constants.MIN_NUMBER_MUSCLES));
        while (outputs.size() < numOutputs && ogOutputs.size() > 0) {
            int randomIndex = (int) (Math.random() * ogOutputs.size());
            Output newOutput = ogOutputs.get(randomIndex);
            ogOutputs.remove(randomIndex);
            newOutput.mutate();
            weight += newOutput.getWeight();
            outputs.add(newOutput);
        }
        for (int newOuts = 0; newOuts < numOutputs - outputs.size(); newOuts++) {
            if (Math.random() < 0.5) {
                Muscle muscle = new Muscle();
                weight += muscle.getWeight();
                outputs.add(muscle);
            } else {
                RotateMuscle rotateMuscle = new RotateMuscle();
                weight += rotateMuscle.getWeight();
                outputs.add(rotateMuscle);
            }
        }

        int numInputs = (int) (Math.max((Math.random() * 3) + ogInputs.size() - 1, Constants.MIN_NUMBER_SENSORS));
        while (inputs.size() < numInputs && ogInputs.size() > 0) {
            int randomIndex = (int) (Math.random() * ogInputs.size());
            Input newInput = ogInputs.get(randomIndex);
            ogInputs.remove(randomIndex);
            newInput.mutate();
            weight += newInput.getWeight();
            inputs.add(newInput);
        }
        for (int newIns = 0; newIns < numInputs - inputs.size(); newIns++) {
            if (Math.random() > 0.5) {
                Sensor sensor = new Sensor();
                weight += sensor.getWeight();
                inputs.add(sensor);
            } else {
                ProximitySensor proximitySensor = new ProximitySensor();
                weight += proximitySensor.getWeight();
                inputs.add(proximitySensor);
            }
        }

        neuralNetwork = og.neuralNetwork;
        neuralNetwork.mutate(inputs.size(), outputs.size());
        weight += neuralNetwork.getWeight();

    }


    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillOval(x - (Constants.BODY_CIRCLE_DIAMETER / 2), y - (Constants.BODY_CIRCLE_DIAMETER / 2), Constants.BODY_CIRCLE_DIAMETER, Constants.BODY_CIRCLE_DIAMETER);

        for (Input input : inputs) {
            gc.setFill(Color.BLACK);
            gc.strokeLine(x, y, input.getX(x, rotation), input.getY(y, rotation));
            if (input instanceof ProximitySensor) {
                gc.setFill(Color.RED);
            } else if (input instanceof Sensor) {
                gc.setFill(Color.BLACK);
            }
            gc.fillOval(input.getX(x, rotation) - (Constants.SENSOR_CIRCLE_DIAMETER / 2), input.getY(y, rotation) - (Constants.SENSOR_CIRCLE_DIAMETER / 2), Constants.SENSOR_CIRCLE_DIAMETER, Constants.SENSOR_CIRCLE_DIAMETER);
        }

        for (Output output : outputs) {
            gc.setFill(Color.BLACK);
            gc.strokeLine(x, y, output.getX(x, rotation), output.getY(y, rotation));
            if (output instanceof RotateMuscle) {
                double footRotation = (rotation + output.getDegree() + (((RotateMuscle) output).getDirection() * -90)) % 360;
                gc.strokeLine(output.getX(x, rotation), output.getY(y, rotation), Constants.calcX(output.getX(x, rotation), footRotation, Constants.ROTATE_MUSCLE_FOOT_LENGTH), Constants.calcY(output.getY(y, rotation), footRotation, Constants.ROTATE_MUSCLE_FOOT_LENGTH));
            }
        }
    }

    public void drawNeuralNetwork(GraphicsContext gc) {
        neuralNetwork.draw(gc);
    }

    public double tick(Food food) {

        double smallestX = Constants.BODY_CIRCLE_DIAMETER / 2;
        double highestX = Constants.BODY_CIRCLE_DIAMETER / 2;
        double smallestY = Constants.BODY_CIRCLE_DIAMETER / 2;
        double highestY = Constants.BODY_CIRCLE_DIAMETER / 2;
        double[] inputValues = new double[inputs.size()];
        for (int in = 0; in < inputs.size(); in++) {
            double sx = inputs.get(in).getX(x, rotation);
            smallestX = Math.min(smallestX, sx - x);
            highestX = Math.max(highestX, sx - x);
            double sy = inputs.get(in).getY(y, rotation);
            smallestY = Math.min(smallestY, sy - y);
            highestY = Math.max(highestY, sy - y);
            if (inputs.get(in) instanceof Sensor) {
                double distanceToFood = Math.hypot(food.getX() - sx, food.getY() - sy);
                double signalStrength = 0;
                if (distanceToFood <= Constants.MAX_FOOD_SENSE_DISTANCE && distanceToFood > 0) {
                    signalStrength = (Constants.MAX_FOOD_SENSE_DISTANCE - distanceToFood) / Constants.MAX_FOOD_SENSE_DISTANCE;
                    signalStrength = Math.pow(signalStrength, 2); //TODO
                }
                inputValues[in] = signalStrength;
            } else if (inputs.get(in) instanceof ProximitySensor) {
                double minDistance = Math.min(Math.min(sx, sy), Math.min(Constants.CANVAS_SIZE - sx, Constants.CANVAS_SIZE - sy));
                double signalStrength = 0;
                if (minDistance > 0 && minDistance <= Constants.MAX_PROXIMITY_SENSE_DISTANCE) {
                    signalStrength = (Constants.MAX_PROXIMITY_SENSE_DISTANCE - minDistance) / Constants.MAX_PROXIMITY_SENSE_DISTANCE;
                    signalStrength = Math.pow(signalStrength, 2); //TODO
                }
                inputValues[in] = signalStrength;
            }
        }

        double[] outputValues = neuralNetwork.getOutputs(inputValues, 1);

        double newX = x;
        double newY = y;
        for (int out = 0; out < outputs.size(); out++) {
            double mx = outputs.get(out).getX(x, rotation);
            smallestX = Math.min(smallestX, mx - x);
            highestX = Math.max(highestX, mx - x);
            double my = outputs.get(out).getY(y, rotation);
            smallestY = Math.min(smallestY, my - y);
            highestY = Math.max(highestY, my - y);
            if (outputs.get(out) instanceof RotateMuscle) {
                RotateMuscle rotateMuscle = (RotateMuscle) outputs.get(out);
                double forceOutput = rotateMuscle.getMaxForce() * outputValues[out];
                rotation = (rotation + (forceOutput * rotateMuscle.getDirection())) % 360;
            }
        }
        for (int out = 0; out < outputs.size(); out++) {
            double mx = outputs.get(out).getX(x, rotation);
            smallestX = Math.min(smallestX, mx - x);
            highestX = Math.max(highestX, mx - x);
            double my = outputs.get(out).getY(y, rotation);
            smallestY = Math.min(smallestY, my - y);
            highestY = Math.max(highestY, my - y);
            if (outputs.get(out) instanceof Muscle) {
                Muscle muscle = (Muscle) outputs.get(out);
                double forceOutput = muscle.getMinForce() + ((muscle.getMaxForce() - muscle.getMinForce()) * outputValues[out]);
                double outputDirection = (muscle.getDegree() + rotation + 180) % 360;
                newX = Constants.calcX(newX, outputDirection, forceOutput);
                newY = Constants.calcY(newY, outputDirection, forceOutput);
            }
        }
        double oldX = x;
        double oldY = y;
        x = Math.min(Constants.CANVAS_SIZE - highestX - (Constants.SENSOR_CIRCLE_DIAMETER / 2), Math.max(0 - smallestX + (Constants.SENSOR_CIRCLE_DIAMETER / 2), newX));
        y = Math.min(Constants.CANVAS_SIZE - highestY - (Constants.SENSOR_CIRCLE_DIAMETER / 2), Math.max(0 - smallestY + (Constants.SENSOR_CIRCLE_DIAMETER / 2), newY));
        double distanceTravelled = Math.hypot(oldX - x, oldY - y);
        foodCount -= distanceTravelled * weight + weight;
        if (foodCount <= 0) {
            alive = false;
        }
        return distanceTravelled;
    }

    public Boolean nearFood(Food food) {
        if (Math.hypot(food.getX() - x, food.getY() - y) <= (Constants.BODY_CIRCLE_DIAMETER / 2) + (Constants.FOOD_CIRCLE_DIAMETER / 2)) {
            foodCount += Constants.FOOD_RESTORE_AMOUNT;
            return true;
        }
        return false;
    }

    public void revive() {
        alive = true;
        x = Constants.CANVAS_SIZE / 2;
        y = Constants.CANVAS_SIZE / 2;
        rotation = 0;
        foodCount = Constants.DEFAULT_FOOD_COUNT;
    }


    public Boolean isAlive() {
        return alive;
    }

    @Override
    public String toString() {
        return "ID: " + ID + "\nGeneration: " + generation + "\nSpecies: " + getSpecies();
    }

    public String getSpecies() {
        return "I-" + inputs.size() + " O-" + outputs.size() + " N-" + neuralNetwork.getNeuronCount();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public List<Input> getInputs() {
        return inputs;
    }
}
