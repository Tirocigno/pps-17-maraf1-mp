package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.ClientController;
import it.unibo.pps2017.client.controller.ClientController$;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlayGameView extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
            final FXMLLoader loader;
			loader = new FXMLLoader(PlayGameView.class.getResource(PlayGameViewUtils.getPlayGameViewFxml()));
			Parent root = loader.load();
            final PlayGameController gameController = loader.getController();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setFullScreen(true);
			primaryStage.setMinHeight(PlayGameViewUtils.getMinHeight());
			primaryStage.setMinWidth(PlayGameViewUtils.getMinWidth());
			ClientController clientController = ClientController$.MODULE$.getSingletonController();
            clientController.setPlayGameController(gameController);
            gameController.setGameController(clientController.getGameController());
			clientController.startActorSystem("127.0.0.1", "127.0.0.1");
			clientController.createRestClient("127.0.0.1", PlayGameViewUtils.getDiscoveryPort());
            clientController.sendMatchRequest();
			primaryStage.show();

        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
