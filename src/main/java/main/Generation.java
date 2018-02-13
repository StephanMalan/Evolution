package main;

import javafx.application.Platform;
import javafx.scene.text.Text;

import java.io.*;
import java.util.List;

public class Generation implements Serializable {

    private Text statusText;
    private DatabaseHandler databaseHandler;
    private int generation;

    public Generation(Text statusText, DatabaseHandler databaseHandler, int generation) {
        this.statusText = statusText;
        this.databaseHandler = databaseHandler;
        this.generation = generation;
    }

    public void generate() {
        if (generation == 1) {
            for (int id = 1; id <= Constants.SAMPLE_SIZE; id++) {
                Organism organism = new Organism(id);
                organism.generateNew();
                test(organism);
            }
        } else {
            List<Integer> organismIDs = databaseHandler.getOrganismIDs(generation - 1);
            for (int prevOrg = 0; prevOrg < Constants.SAMPLE_SIZE * Constants.TOP_PERC; prevOrg++) {
                Organism ogOrganism = databaseHandler.getOrganism(organismIDs.get(prevOrg));
                for (int copy = 0; copy < Constants.SAMPLE_SIZE / (Constants.SAMPLE_SIZE * Constants.TOP_PERC) - 1; copy++) {
                    Organism organism = new Organism((int) ((ogOrganism.getGeneration() * Constants.SAMPLE_SIZE) + (prevOrg * (Constants.SAMPLE_SIZE / (Constants.SAMPLE_SIZE * Constants.TOP_PERC))) + copy + 1));
                    organism.generateNew(ogOrganism, generation);
                    test(organism);
                }
                Organism organism = ogOrganism;
                organism.revive();
                organism.setGeneration(generation);
                organism.setID((int) (((generation - 1) * Constants.SAMPLE_SIZE) + ((prevOrg + 1) * (Constants.SAMPLE_SIZE / (Constants.SAMPLE_SIZE * Constants.TOP_PERC)))));
                test(organism);
            }
        }
    }

    public void test(Organism organism) {
        Food food;
        int foodCount;
        int finalId = (organism.getID() % Constants.SAMPLE_SIZE) + 1;
        Platform.runLater(() -> statusText.setText("Testing ID: " + finalId + " / " + Constants.SAMPLE_SIZE + " (Gen " + generation + ")"));
        foodCount = 0;
        food = new Food(foodCount);
        double time = 0;
        while (organism.isAlive() && foodCount < 50) {
            time += organism.tick(food);
            if (organism.nearFood(food)) {
                foodCount++;
                food = new Food(foodCount);
                int finalFoodCount = foodCount;
                Platform.runLater(() -> statusText.setText("Testing ID: " + finalId + " (" + finalFoodCount + ") / " + Constants.SAMPLE_SIZE + " (Gen " + generation + ")"));
            }
        }
        databaseHandler.saveToDatabase(organism, foodCount, Math.hypot(organism.getX() - food.getX(), organism.getY() - food.getY()), time);
    }

}
