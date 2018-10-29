package it.unibo.pps2017.core.gui;

import akka.actor.ActorRef;
import it.unibo.pps2017.client.controller.ClientController;
import it.unibo.pps2017.client.controller.ClientController$;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.Map;

public class GenericController {

    @FXML
    Button playButton;
    @FXML
    ListView<String> onlineFriends, onlinePlayers;

    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private static final int DISCOVERY_PORT = 2000;
    private Stage primaryStage;

    public void playGame(){
        primaryStage = (Stage) playButton.getScene().getWindow();
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        try {
            final FXMLLoader loader = new FXMLLoader(PlayGameView.class.getResource("PlayGameView.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.centerOnScreen();

            final PlayGameController gameController = loader.getController();
            //Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            ClientController clientController = ClientController$.MODULE$.getSingletonController();
            clientController.setPlayGameController(gameController);
            gameController.setGameController(clientController.getGameController());
            clientController.startActorSystem("127.0.0.1", "127.0.0.1");
            clientController.createRestClient("127.0.0.1", DISCOVERY_PORT);
            clientController.sendMatchRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOnlineFriendsList(Map<String, ActorRef> friendsList){
            for(String player: friendsList.keySet()){
                onlineFriends.getItems().add(player);
            }
    }

    public void setOnlinePlayersList(Map<String, ActorRef> playerList){
        for(String player: playerList.keySet()){
            onlinePlayers.getItems().add(player);
        }
    }

    public void addNewFriend(){
        String newFriend = onlinePlayers.getSelectionModel().getSelectedItem();
        //addFriend(newFriend);
    }

    public void updateOnlineFriendsList(){
        onlineFriends.getItems().clear();
        Map<String, ActorRef> friendsList = null;//getFriendsList()
        for(String player: friendsList.keySet()){
            onlineFriends.getItems().add(player);
        }
    }

    public void updateOnlinePlayersList(){
        onlinePlayers.getItems().clear();
        Map<String, ActorRef> playerList = null;//getPlayersList()
        for(String player: playerList.keySet()){
            onlinePlayers.getItems().add(player);
        }
    }
}
