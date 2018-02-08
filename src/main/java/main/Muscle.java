package main;


public class Muscle {

    private double degree;
    private double maxForce;
    private double minForce;
    private int num;

    public Muscle(int num) {
        this.num = num;
        degree = ((int) (Math.random() * 36)) * 10; //TODO too uniform???
        maxForce = (Math.random() * (Constants.MAX_MUSCLE_FORCE - (Constants.MIN_MUSCLE_FORCE + Constants.MIN_MUSCLE_FORCE_DIFFERENCE) + 1)) + (Constants.MIN_MUSCLE_FORCE + Constants.MIN_MUSCLE_FORCE_DIFFERENCE);
        minForce = (Math.random() * ((maxForce - Constants.MIN_MUSCLE_FORCE_DIFFERENCE) - Constants.MIN_MUSCLE_FORCE + 1)) + Constants.MIN_MUSCLE_FORCE;
    }

    public Muscle(Muscle muscle, int num) {
        this.num = num;
        degree = ((Math.random() * 10) + muscle.getDegree() - 5) % 360;
        maxForce = Math.max(Constants.MIN_MUSCLE_FORCE + Constants.MIN_MUSCLE_FORCE_DIFFERENCE, Math.min(Constants.MAX_MUSCLE_FORCE, (Math.random() * 0.2) + muscle.maxForce - 0.1));
        minForce = Math.max(Constants.MIN_MUSCLE_FORCE, Math.min(maxForce - Constants.MIN_MUSCLE_FORCE_DIFFERENCE, (Math.random() * 0.2) + muscle.maxForce - 0.1));
    }

    public double getDegree() {
        return degree;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public double getMinForce() {
        return minForce;
    }

    public int getNum() {
        return num;
    }
}
