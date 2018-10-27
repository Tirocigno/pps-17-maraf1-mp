package it.unibo.pps2017.core.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationView extends Application{

    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 400;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            final FXMLLoader loader = new FXMLLoader(RegistrationView.class.getResource("registration.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(false);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
