package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.ClientController;
import it.unibo.pps2017.client.controller.ClientController$;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlayGameView extends Application {

	private static final int MIN_WIDTH = 900;
	private static final int MIN_HEIGHT = 685;

	@Override
	public void start(Stage primaryStage) {
		try {
            final FXMLLoader loader = new FXMLLoader(PlayGameView.class.getResource("PlayGameView.fxml"));
            Parent root = loader.load();
            final PlayGameController gameController = loader.getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			//primaryStage.setFullScreen(true);
			primaryStage.setMinHeight(MIN_HEIGHT);
			primaryStage.setMinWidth(MIN_WIDTH);
            ClientController c = ClientController$.MODULE$.getSingletonController();
            c.setPlayGameController(gameController);
            gameController.setGameController(c.getGameController());
            c.startActorSystem("192.168.1.80");
            c.createRestClient("192.168.1.80", 2000);
            c.sendMatchRequest();
			primaryStage.show();


        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
