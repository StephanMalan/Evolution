package main;

public class Sensor{

    private double degree;
    private double sensoryAmplification;
    private int num;

    public Sensor(int num) {
        this.num = num;
        degree = (Math.random() * 36) * 10;
        sensoryAmplification = Math.random();
    }

    public Sensor(Sensor sensor, int i) {
        degree = ((Math.random() * 10) + sensor.getDegree() - 5) % 360;
        sensoryAmplification = (Math.random() * 0.01) + sensor.getSensoryAmplification() + 0.005;
        num = i;
    }

    public double getDegree() {
        return degree;
    }

    public double getSensoryAmplification() {
        return sensoryAmplification;
    }

    public int getNum() {
        return num;
    }
}
