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

import java.util.*;

public class SocialController implements SocialGUIController, BasicPlayerOptions{

    @FXML
    Button playButton, viewButton, okComboView, okComboReplay;
    @FXML
    ListView<String> onlineFriends, onlinePlayers;
    @FXML
    Label responseLabel;
    @FXML
    TextArea players;
    @FXML
    ComboBox<String> comboView, comboReplay;

    private static final String INVITATION_INFO = " invited you to play together as ";
    private static final String INVITATION_REQUEST = "Do you want to join him?";
    private static final String WAITING_MSG = "Waiting for friend response ...";
    private static final String POSITIVE_ANSWER = "positive";
    private static final String NEGATIVE_ANSWER = "false";
    private static final String ACCEPT_MSG = " accepted the invitation!";
    private static final String REJECT_MSG = " rejected the invitation!";
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private static final int DISCOVERY_PORT = 2000;


    private String getSelection(ListView<String> listView){
        return listView.getSelectionModel().getSelectedItem();
    }

    private String getSelection(ComboBox<String> comboBox){
        return comboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Handles the click of addFriend button by adding the player as friend
     */
    public void addNewFriend(){
        String friendAdded = getSelection(onlinePlayers);
        hideReplayMatch();
        hideViewMatch();
        //addFriend(newFriend);
    }

    /**
     * Handles the click of inviteFriend button by sending to the friend the
     * invitation to play together
     */
    public void invitePlayer(){
        String playerInvited = getSelection(onlineFriends);
        hideReplayMatch();
        hideViewMatch();
        //sendPlayerInvitation(playerInvited);
    }

    @Override
    public void notifyErrorOccurred(String errorToNotify) {
        showAlertMessage(errorToNotify);
    }

    @Override
    public void updateOnlineFriendsList(List<String> friendList) {
        onlineFriends.getItems().clear();
        onlineFriends.getItems().addAll(friendList);
    }

    @Override
    public void updateOnlinePlayersList(List<String> playersList) {
        onlinePlayers.getItems().clear();
        onlinePlayers.getItems().addAll(playersList);
    }

    @Override
    public void updateParty(java.util.Map<String, String> partyMap) {
        String label = "All players: ";
        for(java.util.Map.Entry<String,String> entry : partyMap.entrySet()){
            label += entry.getKey() + " (" + entry.getValue() +")\t";
        }
        players.setText(label);
    }

    @Override
    public void notifyMessageResponse(String sender, String responseResult, String request) {
        if(responseResult.equals(POSITIVE_ANSWER)){
            responseLabel.setText(sender + ACCEPT_MSG);
        }
        else if(responseResult.equals(NEGATIVE_ANSWER)){
            responseLabel.setText(sender + REJECT_MSG);
        }
        showAndHideTextResponse();
    }

    @Override
    public void displayRequest(String sender, String role) {
        responseLabel.setText(WAITING_MSG);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, sender + INVITATION_INFO + role
                + ". " + INVITATION_REQUEST, ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                // player accepted to play
            } else {
                // player refused the invitation
            }
        }
    }

    @Override
    public void notifyAPIResult(String message) {
        showAlertMessage(message);
    }

    private void showAndHideTextResponse(){
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        sleeper.setOnSucceeded(event -> responseLabel.setText(""));
        new Thread(sleeper).start();
    }

    private void showAlertMessage(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }


    /**
     * Handles the click of play button so that the player
     * can play a non-competitive game without effect in ranking
     */
    public void handlePlayMatch(){
        playMatch(false);
        //set non competitive msg
    }

    /**
     * Handles the click of playCompetitive button so that the player
     * can play a competitive game with effect in ranking
     */
    public void handlePlayCompetitiveMatch(){
        playMatch(true);
        //set competitive msg
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

    @Override
    public void watchMatch() {
       //create view actor
    }

    /**
     * Handles the click of viewMatch button by showing a combobox
     * and an ok button in way to choose the game to watch
     */
    public void viewMatch(){
        List<String> matches = new ArrayList<>();
        // get matches
        comboView.getItems().clear();
        comboView.getItems().addAll(matches);
        hideReplayMatch();
        showViewMatch();
    }


    /**
     * Handles the click of ok button in the view combobox
     * redirecting the player to the game to watch
     */
    public void okViewMatch(){
        if(getSelection(comboView) != null){
            System.out.println(getSelection(comboView));
        }
        //start view match
    }

    /**
     * Handles the click of replayMatch button by showing a combobox
     * and an ok button in way to choose the game to replay
     */
    public void replayMatch(){
        List<String> matches = new ArrayList<>();
        //get list matches
        comboView.getItems().clear();
        comboReplay.getItems().addAll(matches);
        hideViewMatch();
        showReplayMatch();
    }

    /**
     * Handles the click of ok button in the replay combobox
     * redirecting the player to the game to replay
     */
    public void okReplayMatch(){
        if(getSelection(comboReplay) != null){
            System.out.println(getSelection(comboReplay));
        }
        //start view match
    }

    private void hideViewMatch(){
        comboView.setVisible(false);
        okComboView.setVisible(false);
    }

    private void showViewMatch(){
        comboView.setVisible(true);
        okComboView.setVisible(true);
    }

    private void hideReplayMatch(){
        comboReplay.setVisible(false);
        okComboReplay.setVisible(false);
    }

    private void showReplayMatch(){
        comboReplay.setVisible(true);
        okComboReplay.setVisible(true);
    }

}
