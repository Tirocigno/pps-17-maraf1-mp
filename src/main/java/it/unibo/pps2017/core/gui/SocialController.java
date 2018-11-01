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
import java.util.List;
import java.util.Optional;

public class SocialController implements SocialGUIController, BasicPlayerOptions{

    @FXML
    Button playButton;
    @FXML
    ListView<String> onlineFriends, onlinePlayers;
    @FXML
    Label responseLabel;

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


    public void addNewFriend(){
        String friendAdded = onlineFriends.getSelectionModel().getSelectedItem();
        System.out.println(friendAdded);
        //addFriend(newFriend);
    }

    public void invitePlayer(){
        String playerInvited = onlinePlayers.getSelectionModel().getSelectedItem();
        System.out.println(playerInvited);
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
        for(java.util.Map.Entry<String,String> entry : partyMap.entrySet()){
            String label = "Player: " + entry.getKey() + " Role: " + entry.getValue();
        }
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

    private void showAlertMessage(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void handlePlayMatch(){
        playMatch(false);
        //set non competitive msg
    }
    @FXML
    private void handlePlayCompetitiveMatch(){
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
    public void viewMatch() {

    }

    @FXML
    private void replayMatch(){}

}
