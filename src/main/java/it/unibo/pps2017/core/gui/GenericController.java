package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.Controller;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.view.GenericGUIController;
import it.unibo.pps2017.commons.remote.game.MatchNature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.util.List;

public class GenericController implements GenericGUIController, BasicPlayerOptions{

    @FXML
    Button playButton, viewButton, goButton;
    @FXML
    ListView<String> matchesList;

    private ClientController clientController;
    private static final String ERROR_MATCH_MSG = "Make sure you have selected the match!";

    @FXML
    private void handlePlayMatch(){
        playNonCompetitiveMatch();
    }

    @Override
    public void playNonCompetitiveMatch() {
        scala.Option<scala.collection.immutable.Map<java.lang.String, java.lang.String>> paramMap =
                scala.Option.apply(null);
            clientController.sendMatchRequest(MatchNature.CasualMatch$.MODULE$, paramMap);
    }

    @FXML
    private void handleWatchMatch(){
        watchMatch();
    }

    @Override
    public void watchMatch() {
        clientController.fetchCurrentMatchesList();
    }

    /**
     * Handles the click of go button when a guest player wants to watch an online match.
     * If nothing selected it shows an alert message
     */
    public void goViewMatch(){
        String matchSelected = matchesList.getSelectionModel().getSelectedItem();
        try {
            if (!matchesList.getSelectionModel().getSelectedItem().isEmpty()) {
                clientController.startMatchWatching(matchSelected);
            }
        } catch (Exception ex){
            showAlertMessage(ERROR_MATCH_MSG);
        }
    }

    @Override
    public void setController(Controller controller) {
        this.clientController = (ClientController) controller;
    }

    @Override
    public void notifyError(Throwable throwable) {
        showAlertMessage(throwable.getMessage());
    }

    private void showAlertMessage(String msg){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            alert.showAndWait();
        });

    }

    @Override
    public void displayMatchesList(List<String> matchesList) {
        this.matchesList.getItems().clear();
        this.matchesList.getItems().addAll(matchesList);
    }
}
