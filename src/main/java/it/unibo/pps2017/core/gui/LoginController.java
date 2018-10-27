package it.unibo.pps2017.core.gui;


import it.unibo.pps2017.client.controller.ClientController;
import it.unibo.pps2017.client.controller.ClientController$;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController implements Login {
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private static final int DISCOVERY_PORT = 2000;
    private Stage primaryStage;

    @FXML
    Button loginButton, playButton, viewButton;
    @FXML
    TextField usernameText;
    @FXML
    PasswordField pwdText;
    @FXML
    Label loginOkLabel, errorLabel ;


    @Override
    public void checkLogin() {
        System.out.println("Login pressed");
        if(usernameText.getText().equals("admin") && pwdText.getText().equals("admin")) {
            errorLabel.setVisible(false);
            loginOkLabel.setVisible(true);
            usernameText.setDisable(true);
            pwdText.setDisable(true);
            loginButton.setDisable(true);
            playButton.setDisable(false);
            viewButton.setDisable(false);
        }
        else {
            errorLabel.setVisible(true);
        }


    }

    public void playGame(){
        primaryStage = (Stage) loginButton.getScene().getWindow();
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
}
