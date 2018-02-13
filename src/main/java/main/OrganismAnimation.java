package main;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

public class OrganismAnimation implements Runnable {

    private Organism organism;
    private GraphicsContext gc1;
    private GraphicsContext gc2;
    volatile BooleanProperty running;
    private volatile boolean paused;
    private final Object pauseLock;
    private Food food;
    private int foodCount;
    private double playbackSpeed;

    public OrganismAnimation(Organism organism, GraphicsContext gc1, GraphicsContext gc2) {
        this.organism = organism;
        organism.revive();
        this.gc1 = gc1;
        this.gc2 = gc2;
        running = new SimpleBooleanProperty(true);
        paused = true;
        pauseLock = new Object();
        food = new Food(0);
        foodCount = 0;
        playbackSpeed = 1;
    }

    @Override
    public void run() {
        gc1.setFill(Color.LIGHTBLUE);
        gc1.fillRect(0, 0, Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
        while (running.get()) {
            try {
                synchronized (pauseLock) {
                    if (!running.get()) {
                        break;
                    }
                    if (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException ex) {
                            break;
                        }
                        if (!running.get())
                            break;
                    }
                }
                Platform.runLater(() -> {
                    gc1.clearRect(0, 0, Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
                    gc1.setFill(Color.LIGHTBLUE);
                    gc1.fillRect(0, 0, Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
                    gc1.setFill(Color.GREEN);
                    gc1.fillOval(food.getX() - (Constants.FOOD_CIRCLE_DIAMETER / 2), food.getY() - (Constants.FOOD_CIRCLE_DIAMETER / 2), Constants.FOOD_CIRCLE_DIAMETER, Constants.FOOD_CIRCLE_DIAMETER);
                    organism.draw(gc1);
                    gc2.clearRect(0, 0, Constants.NEURON_CANVAS_SIZE, Constants.NEURON_CANVAS_SIZE);
                    organism.drawNeuralNetwork(gc2);
                });
                Thread.sleep((long) (20 / playbackSpeed));
                organism.tick(food);
                if (organism.nearFood(food)) {
                    foodCount++;
                    food = new Food(foodCount);
                }
                if (!organism.isAlive()) {
                    Thread.sleep((long) (500 / playbackSpeed));
                    food = new Food(0);
                    foodCount = 0;
                    organism.revive();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stop() {
        running.set(false);
        resume();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPlaybackSpeed(double playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

}
