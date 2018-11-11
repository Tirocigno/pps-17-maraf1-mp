package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.Controller;
import it.unibo.pps2017.client.controller.clientcontroller.ClientController;
import it.unibo.pps2017.client.view.login.LoginGUIController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RegistrationLoginController implements LoginGUIController, Registration, Login {

    @FXML
    Button loginButton, registerButton;
    @FXML
    TextField usernameLogin, usernameRegistration;
    @FXML
    PasswordField pwdLogin, pwdRegistration;
    @FXML
    ImageView loginImg, registrationImg;

    private static final String LOGIN_IMG_PATH = "images/login.jpg";
    private static final String REGISTRATION_IMG_PATH = "images/registration.jpg";
    private ClientController clientController;

    @FXML
    public void initialize() {
        Image loginImage = new Image(this.getClass().getResourceAsStream(LOGIN_IMG_PATH));
        Image registrationImage = new Image(this.getClass().getResourceAsStream(REGISTRATION_IMG_PATH));

        this.loginImg.setImage(loginImage);
        this.registrationImg.setImage(registrationImage);
    }

    @FXML
    private void handleCheckLogin(){
        checkLogin();
    }

    @Override
    public void checkLogin() {
        clientController.sendLoginRequest(usernameLogin.getText(), pwdLogin.getText());
    }

    @FXML
    private void handleUserRegistration(){
        registerUser();
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

    /**
     * Method that changes the GUI scene from login/registration scene to generic scene of guest player
     */
    public void startGenericGUI() {
        clientController.startGenericGUI();
    }

    private void showAlertMessage(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.showAndWait();
        });
    }

}
