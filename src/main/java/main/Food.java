package main;

public class Food {

    private double x,y;

    public Food(double ox, double oy) {
        /*x = ox;
        y = oy;
        while (Math.hypot(x - ox, y - oy) != 200) {
            x = (Math.random() * 801) + 100;
            y = (Math.random() * 801) + 100;
        }*/
        if (ox < 500 && oy < 500) {
            x = 650;
            y = 650;
        } else if (ox < 500) {
            x = 650;
            y = 350;
        } else if (oy < 500) {
            x = 350;
            y = 650;
        } else {
            x = 350;
            y = 350;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
