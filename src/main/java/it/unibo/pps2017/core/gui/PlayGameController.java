package it.unibo.pps2017.core.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PlayGameController {

	private static final String COMMANDS_PATH = "src/main/java/it/unibo/pps2017/core/gui/commands/";
	private static final String FORMAT = ".png";

	@FXML
	ImageView wallpaper;

	@FXML
	ImageView firstCard = new ImageView();

	@FXML
	ImageView secondCard = new ImageView();

	@FXML
	ImageView thirdCard = new ImageView();

	@FXML
	ImageView fourthCard = new ImageView();

	@FXML
	ImageView fifthCard = new ImageView();

	@FXML
	ImageView sixthCard = new ImageView();

	@FXML
	ImageView seventhCard = new ImageView();

	@FXML
	ImageView eighthCard = new ImageView();

	@FXML
	ImageView ninthCard = new ImageView();

	@FXML
	ImageView tenthCard = new ImageView();

	@FXML
	Button buttonStart;

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

	@FXML
	ImageView userOneField;

	@FXML
	ImageView userTwoField;

	@FXML
	ImageView userThreeField;

	@FXML
	ImageView userFourField;

	@FXML
	Label timer;

	List<ImageView> userCards;

	private List<String> firstUserCards;

	public PlayGameController() {
		
		/* Simulo il fatto di avere la mia lista di carte. Quando questa mi verra' passata dal controller
		 * eliminero' tutto cio'
		 */
		this.firstUserCards = new ArrayList<>();
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/10spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/9spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/8spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/7spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/6spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/5spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/4spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/3spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/2spade.png");
		firstUserCards.add("src/main/java/it/unibo/pps2017/core/gui/cards/1spade.png");
	}

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
		System.out.println(file);

		/*
		 * per vedere che funziona getCommand(new User("User4"), new Command("busso"));
		 */
	}

	/*
	 * metodo temporaneo che eliminero' quando ricevero' la lista delle carte dal
	 * controller
	 */
	public void distributedCards(final ActionEvent buttonPressed) throws InterruptedException {
		getCardsFirstUser(firstUserCards);

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

	/**
	 * Method to show which card is pressed and throw in field
	 * 
	 * @param clickedCard
	 */
	public void clickedCard(final MouseEvent clickedCard) {
		ImageView playedCard = (ImageView) clickedCard.getSource();
		@SuppressWarnings("deprecation")
		File file = new File(playedCard.getImage().impl_getUrl().substring(5));
		Image userCommand = new Image(file.toURI().toString());
		userOneField.setImage(userCommand);
		playedCard.setVisible(false);

	}

	/**
	 * Method to show the first hand of first user
	 * @param firstUserCards path's list of user hand
	 */
	public void getCardsFirstUser(final List<String> firstUserCards) {

		for (int i = 0; i < 10; i++) {
			
			File file = new File(firstUserCards.get(i));
			Image userCard = new Image(file.toURI().toString());

			switch (i) {
			case 0:
				firstCard.setImage(userCard);
			case 1:
				secondCard.setImage(userCard);
			case 2:
				thirdCard.setImage(userCard);
			case 3:
				fourthCard.setImage(userCard);
			case 4:
				fifthCard.setImage(userCard);
			case 5:
				sixthCard.setImage(userCard);
			case 6:
				seventhCard.setImage(userCard);
			case 7:
				eighthCard.setImage(userCard);
			case 8:
				ninthCard.setImage(userCard);
			case 9:
				tenthCard.setImage(userCard);
			}

		}
	}

}
