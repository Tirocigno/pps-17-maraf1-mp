package it.unibo.pps2017.core.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PlayGameView extends Application {

	private static final int MIN_WIDTH = 900;
	private static final int MIN_HEIGHT = 685;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("PlayGameView.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setFullScreen(true);
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
