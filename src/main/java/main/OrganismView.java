package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OrganismView extends HBox {

    private Organism organism;
    OrganismAnimation animation;

    public OrganismView(Organism organism) {
        this.organism = organism;
        start();
    }

    private void start() {

        Canvas canvas = new Canvas(Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
        VBox canvasPane = new VBox(canvas);
        canvasPane.setPadding(new Insets(10));
        canvasPane.setAlignment(Pos.CENTER);

        TextArea outputTextArea = new TextArea(organism.toString());
        outputTextArea.setEditable(false);
        outputTextArea.setMaxHeight(100);
        HBox textAreaPane = new HBox(outputTextArea);
        textAreaPane.setPadding(new Insets(10));
        textAreaPane.setAlignment(Pos.CENTER);

        Button playButton = new Button("Play");
        Slider playbackSlider = new Slider(0.01, 20, 2);
        playbackSlider.valueProperty().addListener(e -> {
            animation.setPlaybackSpeed(playbackSlider.getValue());
        });
        Button exitButton = new Button("Exit to Main screen");
        exitButton.setOnAction(e -> {
            animation.stop();
        });
        HBox buttonPane = new HBox(playButton, playbackSlider, exitButton);
        buttonPane.setSpacing(10);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setAlignment(Pos.CENTER);

        Canvas neuralNetworkCanvas = new Canvas(Constants.NEURON_CANVAS_SIZE, Constants.NEURON_CANVAS_SIZE);
        VBox neuralCanvasPane = new VBox(neuralNetworkCanvas);
        neuralCanvasPane.setPadding(new Insets(10));
        neuralCanvasPane.setAlignment(Pos.CENTER);

        VBox rightPane = new VBox(textAreaPane, buttonPane, neuralCanvasPane);
        VBox.setVgrow(neuralCanvasPane, Priority.ALWAYS);
        rightPane.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(canvasPane, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        animation = new OrganismAnimation(organism, canvas.getGraphicsContext2D(), neuralNetworkCanvas.getGraphicsContext2D());
        Thread animationThread = new Thread(animation);
        animationThread.start();

        playButton.setOnAction(e -> {
            if (animation.isPaused()) {
                playButton.setText("Pause");
                animation.resume();
            } else {
                playButton.setText("Play");
                animation.pause();
            }
        });
    }

}
