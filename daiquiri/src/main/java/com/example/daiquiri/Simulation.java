package com.example.daiquiri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Simulation extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("habitat-view.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        Controller controller = loader.getController();
        controller.initialize(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Simulation");
        primaryStage.setResizable(true);
        Image icon = new Image(Objects.requireNonNull(Simulation.class.getResourceAsStream("icon.jpg")));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
