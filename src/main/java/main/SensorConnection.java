package main;

public class SensorConnection {

    private Sensor sensor;
    private Axon axon;
    private double threshold;
    private double amplification;

    public SensorConnection(Sensor sensor, Axon axon) {
        this.sensor = sensor;
        this.axon = axon;
        threshold = Math.random() * (Constants.MAX_SENSOR_STRENGTH - 1);
        amplification = Math.random() * 2;
    }

    public Sensor getSensor() {
        return sensor;
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
