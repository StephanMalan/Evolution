package main;

import java.io.Serializable;

public class RotateMuscle extends Output implements Serializable {

    private double maxForce;
    private int direction;

    public RotateMuscle() {
        super();
        direction = Math.random() < 0.5 ? 1 : -1;
        maxForce = Constants.round(Math.random() * Constants.MAX_MUSCLE_ROTATE_FORCE);
    }

    @Override
    public void mutate() {
        maxForce = Math.max(0, maxForce + (Math.random() * 0.2) - 0.1);
        if (Math.random() < 0.1) {
            if (Math.random() < 0.5) {
                setDegree(getDegree() + 5);
            } else {
                setDegree(getDegree() - 5);
            }
        }
        if (Math.random() < 0.1) {
            direction *= -1;
        }
    }

    @Override
    public double getWeight() {
        return maxForce * Constants.MUSCLE_WEIGHT_PER_FORCE;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public int getDirection() {
        return direction;
    }
}
