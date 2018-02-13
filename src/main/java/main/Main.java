package main;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Main extends VBox {

    private DatabaseHandler databaseHandler;
    volatile ObjectProperty<Organism> organism;
    private SpinnerValueFactory.IntegerSpinnerValueFactory intFactory;
    private Text generationText;
    private Text statusText;
    private Button generateOne;
    private Button generateTen;
    private Button generateHundred;
    private Button generateThousand;

    public Main() {
        databaseHandler = new DatabaseHandler();
        organism = new SimpleObjectProperty<>();
        start();
    }

    private void start() {

        //Setup select pane
        Spinner<Integer> generationSpinner = new Spinner<>(0, 0, 0);
        intFactory = (SpinnerValueFactory.IntegerSpinnerValueFactory) generationSpinner.getValueFactory();
        intFactory.setMax(databaseHandler.getNumGenerations());
        intFactory.setMin(0);
        generationText = new Text(" out of " + databaseHandler.getNumGenerations() + " generations");
        HBox selectPane = new HBox(generationSpinner, generationText);
        selectPane.setSpacing(10);
        selectPane.setPadding(new Insets(10));
        selectPane.setAlignment(Pos.CENTER);

        //Setup info pane
        ListView<OrganismResult> generationTableView = new ListView<>();
        generationTableView.setMinWidth(500);
        generationTableView.setPlaceholder(new Label("No generations selected"));
        generationTableView.setCellFactory(param -> new ListCell<OrganismResult>() {
            @Override
            protected void updateItem(OrganismResult item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("ID: %15d   Food count: %5d   Distance: %5.2f   Distance: %5.2f", item.getId(), item.getFoodCount(), item.getTime(), item.getDistanceToNextFood()));
                }
                setOnMouseClicked(evt -> {
                    if (evt.getClickCount() == 2) {
                        organism.setValue(databaseHandler.getOrganism(item.getId()));
                    }
                });
            }
        });
        NumberAxis xAxis = new NumberAxis(1, 20, 50);
        xAxis.setLabel("ID");
        NumberAxis yAxis = new NumberAxis(0, 30, 2);
        yAxis.setLabel("Score");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(false);

        HBox infoPane = new HBox(generationTableView, lineChart);
        HBox.setHgrow(lineChart, Priority.ALWAYS);
        infoPane.setSpacing(50);
        infoPane.setPadding(new Insets(10));
        infoPane.setAlignment(Pos.CENTER);

        //Setup button pane
        generateOne = new Button("Generate 1");
        generateOne.setOnAction(e -> generate(1));
        generateTen = new Button("Generate 10");
        generateTen.setOnAction(e -> generate(10));
        generateHundred = new Button("Generate 100");
        generateHundred.setOnAction(e -> generate(100));
        generateThousand = new Button("Generate 1 000");
        generateThousand.setOnAction(e -> generate(1000));
        HBox buttonPane = new HBox(generateOne, generateTen, generateHundred, generateThousand);
        buttonPane.setSpacing(10);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setAlignment(Pos.CENTER);

        //Setup listeners
        intFactory.valueProperty().addListener(e -> {
            if (intFactory.getValue() > 0 && databaseHandler.getNumGenerations() >= intFactory.getValue()) {
                List<OrganismResult> organisms = databaseHandler.getOrganisms(intFactory.getValue());
                Collections.sort(organisms);
                generationTableView.setItems(FXCollections.observableArrayList(organisms));
                xAxis.setLowerBound((intFactory.getValue() - 1) * Constants.SAMPLE_SIZE + 1);
                xAxis.setUpperBound(intFactory.getValue() * Constants.SAMPLE_SIZE);
                lineChart.getData().clear();
                ObservableList<XYChart.Series> generationData = databaseHandler.getGenerationData(intFactory.getValue());
                lineChart.getData().addAll(generationData.get(0), generationData.get(1), generationData.get(2));
            } else {
                generationTableView.getItems().clear();
                lineChart.getData().clear();
            }
        });
        intFactory.setValue(databaseHandler.getNumGenerations());

        //Setup status pane
        statusText = new Text();
        HBox statusPane = new HBox(statusText);
        statusPane.setSpacing(10);
        statusPane.setPadding(new Insets(10));
        statusPane.setAlignment(Pos.CENTER);

        getChildren().addAll(selectPane, infoPane, buttonPane, statusPane);
        VBox.setVgrow(infoPane, Priority.ALWAYS);
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

    }

    private void generate(int num) {
        Thread thread = new Thread(() -> {
            lockButton(true);
            int numGeneration = databaseHandler.getNumGenerations();
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < num; i++) {
                numGeneration++;
                Generation generation = new Generation(statusText, databaseHandler, numGeneration);
                generation.generate();
                databaseHandler.purge();
                updateDisplay();
            }
            int seconds = (int) ((System.currentTimeMillis() - startTime) / 1000);
            int hours = (seconds - (seconds % 3600)) / 3600;
            seconds -= hours * 3600;
            int minutes = (seconds - (seconds % 60)) / 60;
            seconds -= minutes * 60;
            statusText.setText("Completed " + num + " Gens in " + hours + "h " + minutes + "m " + seconds + "s");
            System.out.println("Completed " + num + " Gens in " + hours + "h " + minutes + "m " + seconds + "s");
            lockButton(false);
        });
        thread.start();
    }

    private void lockButton(boolean lock) {
        generateOne.setDisable(lock);
        generateTen.setDisable(lock);
        generateHundred.setDisable(lock);
        generateThousand.setDisable(lock);
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            intFactory.setMin(1);
            intFactory.setMax(databaseHandler.getNumGenerations());
            intFactory.setValue(databaseHandler.getNumGenerations());
            generationText.setText(" out of " + databaseHandler.getNumGenerations() + " generations");
        });
    }

}
