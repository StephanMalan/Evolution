package main;

import java.io.Serializable;

public class Food implements Serializable{

    private double x,y;

    public Food(int foodCount) {
        int num = foodCount % 10;
        switch (num) {
            case 0:
                x = 187;
                y = 617;
                break;
            case 1:
                x = 323;
                y = 184;
                break;
            case 2:
                x = 692;
                y = 107;
                break;
            case 3:
                x = 163;
                y = 395;
                break;
            case 4:
                x = 112;
                y = 290;
                break;
            case 5:
                x = 120;
                y = 169;
                break;
            case 6:
                x = 188;
                y = 485;
                break;
            case 7:
                x = 318;
                y = 642;
                break;
            case 8:
                x = 474;
                y = 234;
                break;
            default:
                x = 664;
                y = 160;
                break;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
