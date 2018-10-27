package it.unibo.pps2017.core.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationLoginController implements Registration, Login {
    @FXML
    Button loginButton, registerButton;
    @FXML
    TextField usernameLogin, usernameRegistration;
    @FXML
    PasswordField pwdLogin, pwdRegistration;

    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 685;
    private Stage primaryStage;

    @Override
    public void checkLogin() {
        System.out.println("Login pressed");
        if(usernameLogin.getText().equals("admin") && pwdLogin.getText().equals("admin")) {
            updateView();
        }
        else {
            //popup error
        }
    }

    private void updateView(){
        primaryStage = (Stage) loginButton.getScene().getWindow();
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        try {
            final FXMLLoader loader = new FXMLLoader(GenericView.class.getResource("genericView.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
