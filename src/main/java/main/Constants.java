package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Constants {

    public static final int MAX_NUMBER_MUSCLES = 15;
    public static final int MIN_NUMBER_MUSCLES = 2;
    public static final double MAX_MUSCLE_FORCE = 0.5;
    public static final double MIN_MUSCLE_FORCE = 0.01D;
    public static final double MIN_MUSCLE_FORCE_DIFFERENCE = 0.02D;
    public static final double MAX_MUSCLE_ROTATE_FORCE = 2;
    public static final double MUSCLE_WEIGHT_PER_FORCE = 0.05;
    public static final int MAX_NUMBER_SENSORS = 15;

    public static final int MIN_NUMBER_SENSORS = 2;
    public static final double SENSOR_WEIGHT = 0.01;

    public static final double MAX_FOOD_SENSE_DISTANCE = 1500;
    public static final double MAX_PROXIMITY_SENSE_DISTANCE = 150;
    public static final double FOOD_RESTORE_AMOUNT = 1000;
    public static final int DEFAULT_FOOD_COUNT = 1000;

    public static final int SAMPLE_SIZE = 100;
    public static final double TOP_PERC = 0.05;

    public static final double BODY_CIRCLE_DIAMETER = 50D;
    public static final double FOOD_CIRCLE_DIAMETER = 30;
    public static final double EXTREMITY_LENGTH = 50;
    public static final double SENSOR_CIRCLE_DIAMETER = 10;
    public static final double CANVAS_SIZE = 900;
    public static final double NEURON_CANVAS_SIZE = 800;
    public static final double ROTATE_MUSCLE_FOOT_LENGTH = 10;

    public static final int MAX_NUM_NEURON_ROWS = 75;
    public static final int MIN_NUM_NEURON_ROWS = 5;
    public static final int MAX_NUM_NEURON = 75;
    public static final int MIN_NUM_NEURON = 5;
    public static final double NEURON_WEIGHT = 0.0001;

    public static final double MAX_NUM_WEIGHT = 10;
    public static final double MIN_NUM_WEIGHT = -10;

    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(10, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double sigmoid(double input, double a) {
        return 1 / (1 + Math.pow(Math.E, a * -input));
    }

    public static double calcX(double ox, double rotation, double distance) {
        return ox + (distance * Math.sin(Math.toRadians(rotation)));
    }

    public static double calcY(double oy, double rotation, double distance) {
        return oy + (distance * Math.cos(Math.toRadians(rotation)));
    }

    public static Organism copyOrganism(Organism oldOrg) {
        Organism newOrg = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(oldOrg);
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            newOrg = (Organism) ois.readObject();
            oos.close();
            bos.close();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newOrg;
    }
}
