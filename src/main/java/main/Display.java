package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Display extends Application {

    private Main main;
    private OrganismView organismView;

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Evolution");
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> System.exit(0));

        main = new Main();
        Scene mainScene = new Scene(main);

        stage.setScene(mainScene);
        stage.show();

        main.organism.addListener(e -> {
            organismView = new OrganismView(main.organism.get());
            stage.hide();
            stage.setScene(new Scene(organismView));
            stage.setMaximized(true);
            stage.show();
            organismView.animation.running.addListener(e2 -> {
                stage.hide();
                stage.setScene(mainScene);
                stage.setMaximized(true);
                stage.show();
            });
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
