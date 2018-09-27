package it.unibo.pps2017.core.gui;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MainController {

	private static final String COMMANDS_PATH = "src/main/java/it/unibo/pps2017/core/gui/commands/";
	private static final String FORMAT = ".png";

	@FXML
	ImageView wallpaper;

	@FXML
	ImageView firstCard;

	@FXML
	ImageView secondCard;

	@FXML
	ImageView thirdCard;

	@FXML
	ImageView fourthCard;

	@FXML
	ImageView fifthCard;

	@FXML
	ImageView sixthCard;

	@FXML
	ImageView seventhCard;

	@FXML
	ImageView eighthCard;

	@FXML
	ImageView ninthCard;

	@FXML
	ImageView tenthCard;

	@FXML
	Button button;

	@FXML
	Button bussoButton;

	@FXML
	Button voloButton;

	@FXML
	Button striscioButton;

	@FXML
	ImageView currentUserCommand;

	@FXML
	ImageView userTwoCommand;

	@FXML
	ImageView userThreeCommand;

	@FXML
	ImageView userFourCommand;

	/**
	 * This method permits to view the command that principal user selected.
	 * Possibilities: busso, striscio, volo
	 * 
	 * @param buttonPressed
	 *            button pressed from principal user
	 * @throws InterruptedException
	 */
	public void signalMyCommands(final ActionEvent buttonPressed) throws InterruptedException {
		Button button = (Button) buttonPressed.getSource();
		String command = button.getText().toLowerCase();
		File file = new File(COMMANDS_PATH + command + FORMAT);
		Image image = new Image(file.toURI().toString());
		createTimeline(currentUserCommand, image);

		/*
		 * per vedere che funziona 
		 * getCommand(new User("User4"), new Command("busso"));
		 */
	}

	/**
	 * Method to create the animation for user's command
	 * 
	 * @param currentUser
	 *            the user called command
	 * @param command
	 *            the command selected by user
	 */
	public void createTimeline(final ImageView currentUser, final Image command) {
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(currentUser.imageProperty(), command)),
				new KeyFrame(Duration.seconds(2), new KeyValue(currentUser.imageProperty(), null)));
		timeline.play();
	}

	/**
	 * Method called by Controller to notify GUI of user's command and show it
	 * 
	 * @param user
	 *            the user called command
	 * @param command
	 *            the command selected by user
	 */
	public void getCommand(final User user, final Command command) {
		File file = new File(COMMANDS_PATH + command.getCommand() + user.getUser() + FORMAT);
		Image userCommand = new Image(file.toURI().toString());
		switch (user.getUser()) {
		case "User2":
			createTimeline(userTwoCommand, userCommand);
		case "User3":
			createTimeline(userThreeCommand, userCommand);
		case "User4":
			createTimeline(userFourCommand, userCommand);
		}
	}

}
