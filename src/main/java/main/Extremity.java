package main;

import java.io.Serializable;

public abstract class Extremity implements Serializable{

    private int degree;

    public Extremity() {
        degree = ((int) (Math.random() * 72)) * 5;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public double getX(double x, double rotation) {
        return Constants.calcX(x, (degree + rotation) % 360, (Constants.EXTREMITY_LENGTH + (Constants.BODY_CIRCLE_DIAMETER / 2)));
    }

    public double getY(double y, double rotation) {
        return Constants.calcY(y, (degree + rotation) % 360, (Constants.EXTREMITY_LENGTH + (Constants.BODY_CIRCLE_DIAMETER / 2)));
    }

    public abstract void mutate();

    public abstract double getWeight();

}
