package com.ossim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Parent root = loadFxml("/fxml/Dashboard.fxml");
        Scene scene = new Scene(root, 1100, 650);

        primaryStage.setTitle("OS Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Switches the entire window content to a new FXML screen.
     * Used by controllers (e.g. sidebar buttons) to navigate between modules.
     */
    public static void switchScreen(String fxmlPath) {
        try {
            Parent root = loadFxml(fxmlPath);
            primaryStage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Parent loadFxml(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(path));
        return loader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}