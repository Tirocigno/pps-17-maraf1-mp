package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.view.GenericGUIController;
import it.unibo.pps2017.commons.remote.game.MatchNature;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class GenericController implements GenericGUIController, BasicPlayerOptions{

    @FXML
    Button playButton, viewButton, goButton;
    @FXML
    ListView<String> matchesList;

    private ClientController clientController;

    @FXML
    private void handlePlayMatch(){
        playMatch(false);
    }

    @Override
    public void playMatch(boolean competitive) {
        scala.Option paramMap = scala.Option.apply(null);
        if(!competitive){
            clientController.sendMatchRequest(MatchNature.CasualMatch$.MODULE$, paramMap);
        }
    }

    @Override
    public void watchMatch() {
        clientController.fetchCurrentMatchesList();
    }

    public void goViewMatch(){
        String matchSelected = matchesList.getSelectionModel().getSelectedItem();
        clientController.startMatchWatching(matchSelected);
    }


    @Override
    public void setController(ClientController controller) {
        this.clientController = controller;
    }

    @Override
    public void notifyError(Throwable throwable) {
        showAlertMessage(throwable.getMessage());
    }

    private void showAlertMessage(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    @Override
    public void displayMatchesList(List<String> matchesList) {
        this.matchesList.getItems().clear();
        this.matchesList.getItems().addAll(matchesList);
    }
}
