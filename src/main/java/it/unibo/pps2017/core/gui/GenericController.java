package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.ClientController;
import it.unibo.pps2017.client.controller.ClientController$;
import it.unibo.pps2017.client.view.SocialGUIController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenericController implements BasicPlayerOptions{

    @FXML
    Button playButton, viewButton, goButton;
    @FXML
    ListView<String> matchesList;

    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private static final int DISCOVERY_PORT = 2000;

    @FXML
    private void handlePlayMatch(){
        playMatch(false);
        //set non competitive msg
    }

    @Override
    public void playMatch(boolean competitive) {
        Stage primaryStage = (Stage) playButton.getScene().getWindow();
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        try {
            final FXMLLoader loader = new FXMLLoader(PlayGameView.class.getResource("PlayGameView.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.centerOnScreen();

            final PlayGameController gameController = loader.getController();
            startActorController(gameController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startActorController(PlayGameController gameController){
        ClientController clientController = ClientController$.MODULE$.getSingletonController();
        clientController.setPlayGameController(gameController);
        gameController.setGameController(clientController.getGameController());
        clientController.startActorSystem("127.0.0.1", "127.0.0.1");
        clientController.createRestClient("127.0.0.1", DISCOVERY_PORT);
        clientController.sendMatchRequest();
    }

    public void handleWatchMatch(){
        watchMatch();
    }

    @Override
    public void watchMatch() {
        //api call
        List<String> matches = new ArrayList<>();
        matchesList.getItems().clear();
        matchesList.getItems().addAll(matches);
    }

    public void goViewMatch(){
        String matchSelected = matchesList.getSelectionModel().getSelectedItem();
        // viewMatch(matchSelected);
    }
}
