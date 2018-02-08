package main;

public class MuscleConnection {

    private Muscle muscle;
    private Axon axon;
    private double threshold;
    private double amplification;

    public MuscleConnection(Muscle muscle, Axon axon) {
        this.muscle = muscle;
        this.axon = axon;
        threshold = Math.random() * (Constants.MAX_SENSOR_STRENGTH - 1);
        amplification = Math.random() * 2;
    }

    public Muscle getMuscle() {
        return muscle;
    }

    public Axon getAxon() {
        return axon;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getAmplification() {
        return amplification;
    }
}
