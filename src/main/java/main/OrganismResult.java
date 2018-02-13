package main;

import java.io.Serializable;
import java.util.Comparator;

public class OrganismResult implements Serializable, Comparable<OrganismResult> {

    private int id;
    private int foodCount;
    private double distanceToNextFood;
    private double distanceScore;
    private double time;
    private double timeScore;

    public OrganismResult(int id, int foodCount, double distanceToNextFood, double time) {
        this.id = id;
        this.foodCount = foodCount;
        this.distanceToNextFood = distanceToNextFood;
        distanceScore = -distanceToNextFood;
        this.time = time;
        timeScore = -time;
    }

    public int getId() {
        return id;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public double getDistanceToNextFood() {
        return distanceToNextFood;
    }

    public double getDistanceScore() {
        return distanceScore;
    }

    public double getTime() {
        return time;
    }

    public double getTimeScore() {
        return timeScore;
    }

    @Override
    public int compareTo(OrganismResult o) {
        return Comparator.comparing(OrganismResult::getFoodCount).thenComparing(OrganismResult::getTimeScore).thenComparing(OrganismResult::getDistanceScore).reversed().compare(this, o);
    }
}
