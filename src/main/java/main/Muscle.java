package main;


import java.io.Serializable;

public class Muscle extends Output implements Serializable {

    private double maxForce;
    private double minForce;

    public Muscle() {
        super();
        maxForce = Constants.round((Math.random() * (Constants.MAX_MUSCLE_FORCE - (Constants.MIN_MUSCLE_FORCE + Constants.MIN_MUSCLE_FORCE_DIFFERENCE) + 1)) + (Constants.MIN_MUSCLE_FORCE + Constants.MIN_MUSCLE_FORCE_DIFFERENCE));
        minForce = Constants.round((Math.random() * ((maxForce - Constants.MIN_MUSCLE_FORCE_DIFFERENCE) - Constants.MIN_MUSCLE_FORCE + 1)) + Constants.MIN_MUSCLE_FORCE);
    }

    @Override
    public void mutate() {
        maxForce += (Math.random() * 0.2) - 0.1;
        minForce = Math.min(maxForce - Constants.MIN_MUSCLE_FORCE_DIFFERENCE, minForce + (Math.random() * 0.2) - 0.1);
        if (Math.random() < 0.1) {
            if (Math.random() < 0.5) {
                setDegree(getDegree() + 5);
            } else {
                setDegree(getDegree() - 5);
            }
        }
    }

    @Override
    public double getWeight() {
        return maxForce * Constants.MUSCLE_WEIGHT_PER_FORCE;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public double getMinForce() {
        return minForce;
    }
}
