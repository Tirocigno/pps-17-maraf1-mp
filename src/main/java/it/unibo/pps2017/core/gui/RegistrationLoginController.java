package it.unibo.pps2017.core.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistrationLoginController implements Registration, Login {
    @FXML
    Button loginButton, registerButton;
    @FXML
    TextField usernameLogin, usernameRegistration;
    @FXML
    PasswordField pwdLogin, pwdRegistration;

    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 520;

    @Override
    public void checkLogin() {
        System.out.println("Login pressed");

        if(usernameLogin.getText().equals("admin") && pwdLogin.getText().equals("admin")) {
            changeViewToSocial();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login error! Try again!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void changeViewToSocial(){
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        try {
            final FXMLLoader loader = new FXMLLoader(SocialView.class.getResource("socialView.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeViewToGeneric(){
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        try {
            final FXMLLoader loader = new FXMLLoader(SocialView.class.getResource("genericView.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
