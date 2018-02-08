package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Display extends Application {

    public void start(Stage stage) throws Exception {

        stage.setTitle("Evolution");
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> System.exit(0));

        Canvas canvas = new Canvas(1000, 1000);
        canvas.setStyle("-fx-border-color: black");

        HBox canvasPane = new HBox(canvas);
        canvasPane.setAlignment(Pos.CENTER);


        TextArea textArea = new TextArea();
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            Thread t1 = new Thread(() -> {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                int idNum = 1;
                TreeMap<Double, Organism> organismMap = new TreeMap<>();
                try {
                    while (true) {

                        //test
                        while (organismMap.size() < 1000) {
                            Food food = new Food(500, 500);
                            int foodCount = 0;
                            Organism organism = new Organism(idNum++);
                            if (idNum % 100000 == 0) {
                                //System.out.println(idNum);
                            }
                            organism.generateNew();
                            while (organism.alive) {
                                try {
                                    organism.tick(food);
                                    if (organism.nearFood(food)) {
                                        food = new Food(organism.getX(), organism.getY());
                                        foodCount++;
                                    }
                                } catch (Exception ex) {
                                    System.out.println("lol");
                                }
                            }
                            /*if (foodCount >= 1) {
                                run = false;
                                System.out.println(organism.getID() + " " + organism.getSpecies() + " did it!");
                                organism.revive();
                                food = new Food(500, 500);
                                while (organism.alive) {
                                    try {
                                        gc.closePath();
                                        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                        gc.setFill(Color.LIGHTBLUE);
                                        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                        gc.setFill(Color.RED);
                                        gc.fillOval(food.getX() - 15, food.getY() - 15, 30, 30);
                                        organism.draw(gc);
                                        textArea.setText(organism.toString());
                                        try {
                                            Thread.sleep(25);
                                        } catch (InterruptedException e1) {
                                            System.out.println("reeep");
                                        }
                                        organism.tick(food);
                                        if (organism.nearFood(food)) {
                                            food = new Food(organism.getX(), organism.getY());
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("lol");
                                    }
                                }
                            }*/
                            if (foodCount >= 1) {
                                organismMap.put((foodCount * 10000) + (10000 / Math.hypot(organism.getX() - food.getX(), organism.getY() - food.getY())), organism);
                                if (organismMap.size() % 100 == 0) {
                                    System.out.println("Map Size: " + organismMap.size());
                                }
                            }
                        }
                        while (true) {
                            Organism prevBest = organismMap.lastEntry().getValue();
                            double prevBestScore = organismMap.lastEntry().getKey();
                            System.out.println("Previous best is: " + prevBestScore + " (" + prevBest.toString() + ")");

                            prevBest.revive();

                            Food bestFood = new Food(500, 500);
                            while (prevBest.alive) {
                                try {
                                    gc.closePath();
                                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                    gc.setFill(Color.LIGHTBLUE);
                                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                    gc.setFill(Color.RED);
                                    gc.fillOval(bestFood.getX() - 15, bestFood.getY() - 15, 30, 30);
                                    prevBest.draw(gc);
                                    textArea.setText(prevBest.toString());
                                    try {
                                        Thread.sleep(25);
                                    } catch (InterruptedException e1) {
                                        System.out.println("reeep");
                                    }
                                    prevBest.tick(bestFood);
                                    if (prevBest.nearFood(bestFood)) {
                                        bestFood = new Food(prevBest.getX(), prevBest.getY());
                                    }
                                } catch (Exception ex) {
                                    System.out.println("lol");
                                }
                            }

                            organismMap = new TreeMap<>();
                            organismMap.put(prevBestScore, prevBest);
                            while (organismMap.size() < 1000) {
                                Food food = new Food(500, 500);
                                int foodCount = 0;
                                Organism organism = new Organism(idNum++);
                                if (idNum % 100 == 0) {
                                    //System.out.println(idNum);
                                }
                                organism.generateNew(prevBest);
                                //System.out.println("New generation");
                                while (organism.alive) {
                                    /*gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                    gc.setFill(Color.LIGHTBLUE);
                                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                    gc.setFill(Color.RED);
                                    gc.fillOval(food.getX() - 15, food.getY() - 15, 30, 30);
                                    organism.draw(gc);
                                    textArea.setText(organism.toString());
                                    try {
                                        Thread.sleep(25);
                                    } catch (InterruptedException e1) {
                                        System.out.println("reeep");
                                    }*/
                                    organism.tick(food);
                                    if (organism.nearFood(food)) {
                                        food = new Food(organism.getX(), organism.getY());
                                        foodCount++;
                                    }
                                }
                                if (foodCount >= 1) {
                                    organismMap.put((foodCount * 10000) + (1000 / Math.hypot(organism.getX() - food.getX(), organism.getY() - food.getY())), organism);
                                    if (organismMap.size() % 100 == 0) {
                                       // System.out.println("Map Size: " + organismMap.size());
                                    }
                                }
                            }
                        }


                        /*for (int i = 0; i < Constants.SAMPLE_SIZE; i++) {

                            Food food = new Food(500, 500);
                            int foodCount = 0;
                            Organism organism = new Organism(idNum++);
                            if (idNum % 500 == 0) {
                                System.out.println(idNum);
                            }
                            organism.generateNew();
                            int cycles = 0;

                            while (organism.alive) {
                                try {
                                    cycles++;
                                *//*gc.closePath();
                                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                gc.setFill(Color.LIGHTBLUE);
                                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                gc.setFill(Color.RED);
                                gc.fillOval(food.getX() - 15, food.getY() - 15, 30, 30);

                                organism.draw(gc);
                                //textArea.setText(organism.toString());
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    System.out.println("50");
                                }*//*
                                    organism.tick(food);
                                    if (organism.nearFood(food)) {
                                        food = new Food(organism.getX(), organism.getY());
                                        foodCount++;
                                    }
                                } catch (Exception ex) {
                                    System.out.println("lol");
                                }
                            }
                            double score = (foodCount * 10000) + (cycles * 10) + Math.hypot(food.getX() - organism.getX(), food.getY() - organism.getY());
                            organismMap.put(score, organism);
                        }
                        System.out.println(organismMap.lastEntry().getValue().getID() + ") " + organismMap.lastEntry().getValue().getSpecies() + ": " + organismMap.lastEntry().getKey());
                        Organism bestOrganism = organismMap.lastEntry().getValue();
                        bestOrganism.revive();
                        Food food = new Food(500, 500);
                        System.out.println(food.getX() + " : " + food.getY());
                        while (bestOrganism.alive) {
                            try {
                                gc.closePath();
                                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                gc.setFill(Color.LIGHTBLUE);
                                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                gc.setFill(Color.RED);
                                gc.fillOval(food.getX() - 15, food.getY() - 15, 30, 30);

                                bestOrganism.draw(gc);
                                textArea.setText(bestOrganism.toString());
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e1) {
                                    System.out.println("50");
                                }
                                bestOrganism.tick(food);
                                if (bestOrganism.nearFood(food)) {
                                    food = new Food(bestOrganism.getX(), bestOrganism.getY());
                                }
                            } catch (Exception ex) {
                                System.out.println("lol");
                            }
                        }*/

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            t1.start();
        });

        VBox infoPane = new VBox(textArea, startButton);
        infoPane.setSpacing(15);
        infoPane.setPadding(new Insets(15));
        infoPane.setAlignment(Pos.TOP_CENTER);

        HBox contentPane = new HBox(canvasPane, infoPane);

        stage.setScene(new Scene(contentPane));
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
