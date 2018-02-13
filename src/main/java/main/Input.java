package main;

import java.io.Serializable;

public abstract class Input extends Extremity implements Serializable  {

    public Input() {
        super();
    }

    public void mutate() {
        //TODO
    }

    public double getWeight() {
        return Constants.SENSOR_WEIGHT;
    }

}
