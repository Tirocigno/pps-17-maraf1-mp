package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.Controller;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController$;
import it.unibo.pps2017.client.controller.socialcontroller.SocialController;
import it.unibo.pps2017.client.controller.socialcontroller.SocialController$;
import it.unibo.pps2017.commons.remote.game.MatchNature;
import it.unibo.pps2017.commons.remote.social.SocialResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class SocialGUIController implements it.unibo.pps2017.client.view.social.SocialGUIController, BasicPlayerOptions{

    @FXML
    Button playButton, playCompetitiveButton, viewButton, replayButton, okComboView, okComboReplay, partnerButton,
            foeButton, addFriendButton;
    @FXML
    ListView<String> onlineFriends, onlinePlayers;
    @FXML
    Label responsePlayLabel, responseFriendLabel, totalPoints;
    @FXML
    TextArea players;
    @FXML
    ComboBox<String> comboView, comboReplay;

    private static final String INVITATION_INFO = " invited you to play together as ";
    private static final String PARTNER = "Partner";
    private static final String FOE = "Foe";
    private static final String INVITATION_REQUEST = "Do you want to join him?";
    private static final String FRIEND_REQUEST = " want to add you as friend. Do you want to accept?";
    private static final String WAITING_MSG = "Waiting ...";
    private static final String POSITIVE_ANSWER = "positive";
    private static final String NEGATIVE_ANSWER = "negative";
    private static final String ACCEPT_MSG = " accepted the invitation!";
    private static final String REJECT_MSG = " rejected the invitation!";
    private static final String ADD_FRIEND = "add friend";
    private static final String INVITE_FRIEND = "invite friend";
    private SocialController socialController;
    private ClientController clientController = ClientController$.MODULE$.getSingletonController();
    private String request;

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
        responseFriendLabel.setText(WAITING_MSG);
        String playerSelected = getSelection(onlinePlayers);
        hideReplayMatch();
        hideViewMatch();
        socialController.tellFriendShipMessage(playerSelected);
    }

    /**
     * Handles the click of inviteFriend button by sending to the friend the
     * invitation to play together as partner
     */
    public void inviteFriendToPlayAsPartner(){
        responsePlayLabel.setText(WAITING_MSG);
        disableReplayViewButtons();
        socialController.tellInvitePlayerAsPartner(getSelectedFriend());
        request = PARTNER;
    }

    /**
     * Handles the click of inviteFriend button by sending to the friend the
     * invitation to play together as foe
     */
    public void inviteFriendToPlayAsFoe(){
        responsePlayLabel.setText(WAITING_MSG);
        disableReplayViewButtons();
        socialController.tellInvitePlayerAsFoe(getSelectedFriend());
        request = FOE;
    }

    private void disableReplayViewButtons(){
        viewButton.setDisable(true);
        replayButton.setDisable(true);
    }

    private void disableGUIButtons(){
        viewButton.setDisable(true);
        replayButton.setDisable(true);
        playButton.setDisable(true);
        playCompetitiveButton.setDisable(true);
        partnerButton.setDisable(true);
        foeButton.setDisable(true);
        addFriendButton.setDisable(true);
    }

    private void enableGUIButtons(){
        viewButton.setDisable(false);
        replayButton.setDisable(false);
        playButton.setDisable(false);
        playCompetitiveButton.setDisable(false);
        partnerButton.setDisable(false);
        foeButton.setDisable(false);
        addFriendButton.setDisable(false);
    }

    private String getSelectedFriend(){
        String friendToInvite = getSelection(onlineFriends);
        hideReplayMatch();
        hideViewMatch();
        return friendToInvite;
    }

    @Override
    public void notifyErrorOccurred(String errorToNotify) {
        showAlertMessage(errorToNotify);
    }

    @Override
    public void updateOnlineFriendsList(List<String> friendList) {
        runSafeOnFXThread(() -> {
            onlineFriends.getItems().clear();
            onlineFriends.getItems().addAll(friendList);
        });
    }

    @Override
    public void updateOnlinePlayersList(List<String> playersList) {
        runSafeOnFXThread(() -> {
            onlinePlayers.getItems().clear();
            onlinePlayers.getItems().addAll(playersList);
        });
    }

    @Override
    public void updateParty(java.util.Map<String, String> partyMap) {
        String label = "All players: ";
        for(java.util.Map.Entry<String,String> entry : partyMap.entrySet()){
            label += entry.getKey() + " (" + entry.getValue() + ")\t";
        }
        players.setText(label);
    }

    @Override
    public void notifyMessageResponse(String sender, String responseResult, String request) {

        runSafeOnFXThread(() -> {
            if (request.equals(SocialController$.MODULE$.FRIEND_REQUEST())) {
                System.out.println("Friend request");
                if (responseResult.equals(POSITIVE_ANSWER)) {
                    responseFriendLabel.setText(sender + ACCEPT_MSG);
                } else if (responseResult.equals(NEGATIVE_ANSWER)) {
                    responseFriendLabel.setText(sender + REJECT_MSG);
                    enableGUIButtons();
                }
                showAndHideTextResponse(responseFriendLabel);
            } else {
                System.out.println("Play request");
                if (responseResult.equals(POSITIVE_ANSWER)) {
                    responsePlayLabel.setText(sender + ACCEPT_MSG);
                } else if (responseResult.equals(NEGATIVE_ANSWER)) {
                    responsePlayLabel.setText(sender + REJECT_MSG);
                }
                showAndHideTextResponse(responsePlayLabel);
            }
        });
    }

    @Override
    public void displayRequest(String sender, String role) {

        runSafeOnFXThread(() -> {
            String message;
            if (role.equals(PARTNER) || role.equals(FOE)) {
                message = sender + INVITATION_INFO + role + ". " + INVITATION_REQUEST;
                showAlertConfirmation(message, INVITE_FRIEND);

            } else if (role.equals(SocialController$.MODULE$.FRIEND_REQUEST())) {
                message = sender + FRIEND_REQUEST;
                showAlertConfirmation(message, ADD_FRIEND);
            }
        });
    }

    private void showAlertConfirmation(String msg, String type){

        runSafeOnFXThread(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (type.equals(ADD_FRIEND)) {
                    if (result.get() == ButtonType.YES) {
                        socialController.notifyFriendMessageResponse(SocialResponse.PositiveResponse$.MODULE$);
                    } else {
                        socialController.notifyFriendMessageResponse(SocialResponse.NegativeResponse$.MODULE$);
                    }
                } else if (type.equals(INVITE_FRIEND)) {
                    if (result.get() == ButtonType.YES) {
                        disableGUIButtons();
                        socialController.notifyInviteMessageResponse(SocialResponse.PositiveResponse$.MODULE$);
                    } else {
                        socialController.notifyInviteMessageResponse(SocialResponse.NegativeResponse$.MODULE$);
                    }
                }

            }
        });
    }

    @Override
    public void notifyAPIResult(String message) {
        showAlertMessage(message);
    }


    private void showAndHideTextResponse(Label label){
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

        sleeper.setOnSucceeded(event -> label.setText(""));
        new Thread(sleeper).start();
    }

    private void showAlertMessage(String msg){
        runSafeOnFXThread(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }


    /**
     * Handles the click of play button so that the player
     * can play a non-competitive game without effect in ranking
     */
    public void handlePlayMatch(){
        playNonCompetitiveMatch();
    }

    /**
     * Handles the click of playCompetitive button so that the player
     * can play a competitive game with effect in ranking
     */
    public void handlePlayCompetitiveMatch(){
        playCompetitiveMatch();
    }

    @Override
    public void playNonCompetitiveMatch() {
            socialController.startGame(MatchNature.CasualMatch$.MODULE$);
    }

    private void playCompetitiveMatch() {
            socialController.startGame(MatchNature.CompetitiveMatch$.MODULE$);
    }

    /**
     * Handles the click of viewMatch button by showing a combobox
     * and an ok button in way to choose the game to watch
     */

    @Override
    public void watchMatch() {
       clientController.fetchCurrentMatchesList();
    }

    public void displayViewMatches(List<String> matches){
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
        if(!getSelection(comboView).isEmpty()){
            clientController.startMatchWatching(getSelection(comboView));
        }
    }

    /**
     * Handles the click of replayMatch button by showing a combobox
     * and an ok button in way to choose the game to replay
     */
    public void replayMatch(){
        clientController.fetchRegisteredMatchesList();
    }

    public void displayReplayMatches(List<String> matches){
        runSafeOnFXThread(() -> {
            comboReplay.getItems().clear();
            comboReplay.getItems().addAll(matches);
            hideViewMatch();
            showReplayMatch();
        });
    }

    @Override
    public void resetGUI() {
        enableGUIButtons();
    }

    @Override
    public void setTotalPoints(int totalPoints) {
        runSafeOnFXThread(() -> this.totalPoints.setText(Integer.valueOf(totalPoints).toString()));
    }

    /**
     * Handles the click of ok button in the replay combobox
     * redirecting the player to the game to replay
     */
    public void okReplayMatch(){
        if(!getSelection(comboReplay).isEmpty()){
            clientController.startMatchReplay(getSelection(comboReplay));
        }
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

    @Override
    public void setController(Controller controller) {
        this.socialController = (SocialController) controller;
    }

    @Override
    public void notifyError(Throwable throwable) {
        runSafeOnFXThread(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, throwable.toString(), ButtonType.OK);
            alert.showAndWait();
        });
    }

    /**
     * Private method for run safe an update on GUI.
     *
     * @param runnable the runnable thread.
     */
    private void runSafeOnFXThread(Runnable runnable) {
        Platform.runLater(runnable);
    }
}
