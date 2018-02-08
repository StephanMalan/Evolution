package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Organism {

    public Boolean alive;
    public double x;
    public double y;
    public int generationNumber;
    public int ID;
    public double foodCount;
    public List<Muscle> muscles;
    public List<Sensor> sensors;
    public List<Axon> axons;
    public List<MuscleConnection> muscleConnections;
    public List<SensorConnection> sensorConnections;

    public Organism(int ID) {
        this.ID = ID;
        alive = true;
        x = 500;
        y = 500;
        foodCount = Constants.DEFAULT_HUNGER;
        muscles = new ArrayList<>();
        sensors = new ArrayList<>();
        axons = new ArrayList<>();
        muscleConnections = new ArrayList<>();
        sensorConnections = new ArrayList<>();
    }

    public void generateNew() {
        try {
            generationNumber = 0;
            int numMuscles = (int) ((Math.random() * ((Constants.MAX_NUMBER_MUSCLES + generationNumber) - Constants.MIN_NUMBER_MUSCLES + 1)) + Constants.MIN_NUMBER_MUSCLES);
            for (int i = 0; i < numMuscles; i++) {
                muscles.add(new Muscle(i));
            }

            int numSensors = (int) ((Math.random() * ((Constants.MAX_NUMBER_SENSORS + generationNumber) - Constants.MIN_NUMBER_SENSORS + 1)) + Constants.MIN_NUMBER_SENSORS);
            for (int i = 0; i < numSensors; i++) {
                sensors.add(new Sensor(i));
            }

            int numAxons = (int) ((Math.random() * ((Constants.MAX_NUMBER_AXONS + generationNumber) - Constants.MIN_NUMBER_AXONS + 1)) + Constants.MIN_NUMBER_AXONS);
            for (int i = 0; i < numAxons; i++) {
                axons.add(new Axon(i));
            }

            for (Sensor sensor : sensors) {
                int numSensorConnections = (int) ((Math.random() * (axons.size() * Constants.MAX_AXON_CONNECTION_PERC)) + 2);
                List<Axon> tempAxons = new ArrayList<>(axons);
                for (int i = 0; i < numSensorConnections; i++) {
                    int rand = (int) (Math.random() * tempAxons.size());
                    sensorConnections.add(new SensorConnection(sensor, tempAxons.get(rand)));
                    tempAxons.remove(rand);
                }
            }

            for (Muscle muscle : muscles) {
                int numMuscleConnections = (int) ((Math.random() * (axons.size() * Constants.MAX_AXON_CONNECTION_PERC)) + 2);
                List<Axon> tempAxons = new ArrayList<>(axons);
                for (int i = 0; i < numMuscleConnections; i++) {
                    int rand = (int) (Math.random() * tempAxons.size());
                    muscleConnections.add(new MuscleConnection(muscle, tempAxons.get(rand)));
                    tempAxons.remove(rand);
                }
            }
        } catch (Exception ex) {
            System.out.println("3");
            alive = false;
        }
    }

    public void generateNew(Organism og) {
        generationNumber = og.generationNumber + 1;

        int numMuscles = (int) ((Math.random() * ((og.muscles.size() + generationNumber) - (og.muscles.size() - generationNumber) + 1)) + (og.muscles.size() + generationNumber));
        for (int i = 0; i < numMuscles; i++) {
            if (i < og.muscles.size()) {
                muscles.add(new Muscle(og.muscles.get(i), i));
            } else {
                muscles.add(new Muscle(i));
            }
        }

        int numSensors = (int) ((Math.random() * ((og.sensors.size() + generationNumber) - (og.sensors.size() - generationNumber) + 1)) + (og.sensors.size() - generationNumber));
        for (int i = 0; i < numSensors; i++) {
            if (i < og.sensors.size()) {
                sensors.add(new Sensor(og.sensors.get(i), i));
            } else {
                sensors.add(new Sensor(i));
            }
        }

        int numAxons = (int) ((Math.random() * ((og.axons.size() + generationNumber) - (og.axons.size() - generationNumber) + 1)) + (og.axons.size() - generationNumber));
        for (int i = 0; i < numAxons; i++) {
            if (i < og.axons.size()) {
                axons.add(new Axon(i));
            } else {
                axons.add(new Axon(i));
            }
        }

        for (Sensor sensor : sensors) {
            List<Axon> tempAxons = new ArrayList<>(axons);
            if (sensor.getNum() > -1) {
                List<SensorConnection> ogSensorConnections = getSensorConnections(og.sensorConnections, sensor.getNum());
                int numSensorConnections = (int) ((Math.random() * (generationNumber * 2)) + (ogSensorConnections.size() - generationNumber));
                //System.out.println("numSensorConnections: " + numSensorConnections);
                for (int i = 0; i < numSensorConnections; i++) {
                    if (i < ogSensorConnections.size() && tempAxons.size() > 0) {
                        Axon tempAxon = getAxon(tempAxons, ogSensorConnections.get(i).getAxon().getNum());
                        sensorConnections.add(new SensorConnection(sensor, tempAxon));
                        tempAxons.remove(tempAxon);
                    } else  if (tempAxons.size() > 0){
                        Axon tempAxon = tempAxons.get((int) (Math.random() * tempAxons.size()));
                        tempAxons.remove(tempAxon);
                        sensorConnections.add(new SensorConnection(sensor, tempAxon));
                    }
                }
            } else  if (tempAxons.size() > 0){
                Axon tempAxon = tempAxons.get((int) (Math.random() * tempAxons.size()));
                tempAxons.remove(tempAxon);
                sensorConnections.add(new SensorConnection(sensor, tempAxon));
            }
        }

        for (Muscle muscle : muscles) {
            List<Axon> tempAxons = new ArrayList<>(axons);
            if (muscle.getNum() > -1) {
                List<MuscleConnection> ogMuscleConnections = getMuscleConnections(og.muscleConnections, muscle.getNum());
                int numMuscleConnections = (int) ((Math.random() * (generationNumber * 2)) + (ogMuscleConnections.size() - generationNumber));
                //System.out.println("numMuscleConnections: " + numMuscleConnections);
                for (int i = 0; i < numMuscleConnections; i++) {
                    if (i < ogMuscleConnections.size() && tempAxons.size() > 0) {
                        Axon tempAxon = getAxon(tempAxons, ogMuscleConnections.get(i).getAxon().getNum());
                        muscleConnections.add(new MuscleConnection(muscle, tempAxon));
                        tempAxons.remove(tempAxon);
                    } else if (tempAxons.size() > 0){
                        Axon tempAxon = tempAxons.get((int) (Math.random() * tempAxons.size()));
                        tempAxons.remove(tempAxon);
                        muscleConnections.add(new MuscleConnection(muscle, tempAxon));
                    }
                }
            } else if (tempAxons.size() > 0){
                Axon tempAxon = tempAxons.get((int) (Math.random() * tempAxons.size()));
                tempAxons.remove(tempAxon);
                muscleConnections.add(new MuscleConnection(muscle, tempAxon));
            }
        }

    }

    public List<SensorConnection> getSensorConnections(List<SensorConnection> sensorConnections, int num) {
        List<SensorConnection> out = new ArrayList<>();
        for (SensorConnection sensorConnection : sensorConnections) {
            if (sensorConnection.getSensor().getNum() == num) {
                out.add(sensorConnection);
            }
        }
        return out;
    }

    public List<MuscleConnection> getMuscleConnections(List<MuscleConnection> muscleConnections, int num) {
        List<MuscleConnection> out = new ArrayList<>();
        for (MuscleConnection muscleConnection : muscleConnections) {
            if (muscleConnection.getMuscle().getNum() == num) {
                out.add(muscleConnection);
            }
        }
        return out;
    }

    public Axon getAxon(List<Axon> axons, int num) {
        for (Axon axon : axons) {
            if (axon.getNum() == num) {
                return axon;
            }
        }
        return axons.get((int) (Math.random() * axons.size()));
    }

    public void draw(GraphicsContext gc) {
        try {
            gc.setFill(Color.BLACK);
            gc.fillOval(x - (Constants.CIRCLE_DIAMETER / 2), y - (Constants.CIRCLE_DIAMETER / 2), Constants.CIRCLE_DIAMETER, Constants.CIRCLE_DIAMETER);
            for (Muscle muscle : muscles) {
                gc.strokeLine(x, y, x + (Constants.EXTREMITY_LENGTH * Math.cos(muscle.getDegree())), y + (Constants.EXTREMITY_LENGTH * Math.sin(muscle.getDegree())));
            }
            for (Sensor sensor : sensors) {
                gc.strokeLine(x, y, x + (Constants.EXTREMITY_LENGTH * Math.cos(sensor.getDegree())), y + (Constants.EXTREMITY_LENGTH * Math.sin(sensor.getDegree())));
                gc.fillOval(x + (Constants.EXTREMITY_LENGTH * Math.cos(sensor.getDegree())) - 5, y + (Constants.EXTREMITY_LENGTH * Math.sin(sensor.getDegree())) - 5, 10, 10);
            }
        } catch (Exception ex) {
            System.out.println("2");
            alive = false;
        }
    }

    public void tick(Food food) {
        try {
            double oldX = x;
            double oldY = y;
            for (Sensor sensor : sensors) {
                double distanceToFood = Math.hypot(food.getX() - x + (Constants.EXTREMITY_LENGTH * Math.cos(sensor.getDegree())), food.getY() - y + (Constants.EXTREMITY_LENGTH * Math.sin(sensor.getDegree())));
                //double signalStrength = Constants.MAX_SENSOR_STRENGTH - (distanceToFood / Constants.FOOD_MAX_SENSE_DISTANCE * Constants.MAX_SENSOR_STRENGTH) * sensor.getSensoryAmplification();
                //double signalStrength = (Constants.MAX_SENSOR_STRENGTH * (Math.pow(Constants.FOOD_MAX_SENSE_DISTANCE, 2) / Math.pow(distanceToFood, 2))) * sensor.getSensoryAmplification();
                //double signalStrength = (Constants.MAX_SENSOR_STRENGTH * (Constants.FOOD_MAX_SENSE_DISTANCE / distanceToFood)) * sensor.getSensoryAmplification();
                double signalStrength = (Constants.MAX_SENSOR_STRENGTH / (Constants.FOOD_MAX_SENSE_DISTANCE / distanceToFood)) * sensor.getSensoryAmplification();
                for (SensorConnection sensorConnection : sensorConnections) {
                    if (sensorConnection.getSensor().equals(sensor) && sensorConnection.getThreshold() < signalStrength) {
                        double ss1 = signalStrength * sensorConnection.getAmplification();
                        for (MuscleConnection muscleConnection : muscleConnections) {
                            if (muscleConnection.getAxon().equals(sensorConnection.getAxon()) && muscleConnection.getThreshold() < ss1) {
                                double ss2 = ss1 * muscleConnection.getAmplification();
                                double outForce = muscleConnection.getMuscle().getMaxForce() - (((muscleConnection.getMuscle().getMaxForce() - muscleConnection.getMuscle().getMinForce()) / Constants.MAX_SENSOR_STRENGTH) * ss2);
                                outForce = Math.min(outForce, muscleConnection.getMuscle().getMaxForce());
                                if (outForce > 0) {
                                    //foodCount -= outForce;
                                    double pushDirection = muscleConnection.getMuscle().getDegree() + 180 % 360;
                                    //System.out.println("x:" + Math.max(0, Math.min(1000, x + (outForce * Math.cos(pushDirection)))));
                                    x = Math.max(0, Math.min(1000, x + (outForce * Math.cos(pushDirection))));
                                    //System.out.println("y: " + Math.max(0, Math.min(1000, y + (outForce * Math.sin(pushDirection)))));
                                    y = Math.max(0, Math.min(1000, y + (outForce * Math.sin(pushDirection))));
                                }
                            }
                        }
                    }
                }
            }
            double distanceTravelled = Math.hypot(oldX - x, oldY - y);
            foodCount -= distanceTravelled;
            foodCount -= (muscles.size() + sensors.size());
            if (foodCount < 0) {
                alive = false;
            }
        } catch (Exception ex) {
            System.out.println("1");
            alive = false;
        }
    }

    public Boolean nearFood(Food food) {
        try {
            if (Math.hypot(x - food.getX(), y - food.getY()) <= Constants.EXTREMITY_LENGTH + (Constants.CIRCLE_DIAMETER / 2)) {
                /*System.out.println("GOT FOOD!");
                System.out.println(axons);
                try {
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage capture = new Robot().createScreenCapture(screenRect);
                    ImageIO.write(capture, "bmp", new File("C:/Test/" + ID + ".bmp"));
                } catch (Exception ex) {
                    System.out.println("test");
                }*/
                foodCount += Constants.FOOD_RESTORE_AMOUNT;
                return true;
            }
        } catch (Exception ex) {
            System.out.println("wtf");
        }
        return false;
    }

    public void revive() {
        alive = true;
        x = 500;
        y = 500;
        foodCount = Constants.DEFAULT_HUNGER;
    }

    @Override
    public String toString() {
        return "ID: " + ID + "\nGeneration: " + generationNumber + "\nFood Level: " + ((int) foodCount) + "\nSpecies: " + getSpecies();
    }

    public String getSpecies() {
        return "S" + sensors.size() + "M" + muscles.size() + "A" + axons.size();
    }

    public int getID() {
        return ID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
