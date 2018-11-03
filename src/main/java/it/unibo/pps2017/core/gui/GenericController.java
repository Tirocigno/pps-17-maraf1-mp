package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController$;
import it.unibo.pps2017.commons.remote.game.MatchNature;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class GenericController implements  BasicPlayerOptions{

    @FXML
    Button playButton, viewButton, goButton;
    @FXML
    ListView<String> matchesList;

    private ClientController clientController = ClientController$.MODULE$.getSingletonController();

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

    public void handleWatchMatch(){
        watchMatch();
    }

    @Override
    public void watchMatch() {
        clientController.fetchCurrentMatchesList();
    }

    public void displayViewMatches(List<String> matches){
        matchesList.getItems().clear();
        matchesList.getItems().addAll(matches);
    }

    public void goViewMatch(){
        String matchSelected = matchesList.getSelectionModel().getSelectedItem();
        clientController.startMatchWatching(matchSelected);
    }


}
