package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.Controller;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.view.login.LoginGUIController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;

public class RegistrationLoginController implements LoginGUIController, Registration, Login {

    @FXML
    Button loginButton, registerButton;
    @FXML
    TextField usernameLogin, usernameRegistration;
    @FXML
    PasswordField pwdLogin, pwdRegistration;
    @FXML
    ImageView loginImage;

    private static final String LOGIN_IMG_PATH = "images/login.jpg";
    private static final String REGISTRATION_IMG_PATH = "images/registration.jpg";
    private ClientController clientController;

    @FXML
    public void initialize() {
       Image image = new Image(this.getClass().getResourceAsStream(LOGIN_IMG_PATH));

       // loginImage = new Image(this.getClass().getResourceAsStream(REGISTRATION_IMG_PATH));
    }

    @Override
    public void checkLogin() {
        clientController.sendLoginRequest(usernameLogin.getText(), pwdLogin.getText());
    }

    @Override
    public void registerUser() {
        clientController.sendRegisterRequest(usernameRegistration.getText(), pwdRegistration.getText());
    }

    @Override
    public void handleResponse(String message) {
        showAlertMessage(message);
    }

    @Override
    public void setController(Controller controller) {
        this.clientController = (ClientController) controller;
    }

    @Override
    public void notifyError(Throwable throwable) {
        showAlertMessage(throwable.getMessage());
    }

    private void showAlertMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
