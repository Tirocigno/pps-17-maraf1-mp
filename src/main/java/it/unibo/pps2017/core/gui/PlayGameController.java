package it.unibo.pps2017.core.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

public class PlayGameController implements PlayGame {

	private static final String COMMANDS_PATH = "src/main/java/it/unibo/pps2017/core/gui/commands/";
	private static final String EMPTY_FIELD = "src/main/java/it/unibo/pps2017/core/gui/cards/emptyField.png";
	private static final String EMPTY_FIELD_MY_TURN = "src/main/java/it/unibo/pps2017/core/gui/cards/emptyFieldMyTurn.png";
	private static final String FORMAT = ".png";
	private static final int TOTAL_HAND_CARDS = 10;

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
	ImageView user1Field;

	@FXML
	ImageView user2Field;

	@FXML
	ImageView user3Field;

	@FXML
	ImageView user4Field;

	@FXML
	Label timer;

	List<ImageView> userCards;

	/*
	 * in questa mappa avro' l'indice della carta e il suo path, mi serve per capire
	 * quando gioco una carta quale e' e dirlo al controller
	 */
	private Map<Integer, String> indexOfMyCards;
	private List<String> firstUserCards;
	private List<User> users;

	public PlayGameController() {

		/*
		 * Simulo il fatto di avere la mia lista di carte. Quando questa mi verra'
		 * passata dal controller eliminero' tutto cio'
		 */
		this.firstUserCards = new ArrayList<>();
		this.users = new ArrayList<>();
		this.users.add(new User("User1"));
		this.users.add(new User("User2"));
		this.users.add(new User("User3"));
		this.users.add(new User("User4"));
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

		this.indexOfMyCards = new HashMap<>();
	}

	/**
	 * This method permits to view the command that first user selected.
	 * Possibilities: busso, striscio, volo.
	 * 
	 * @param buttonPressed
	 *            button pressed from principal user
	 * @throws InterruptedException
	 */
	public void signalMyCommands(final ActionEvent buttonPressed) throws InterruptedException {
		Button button = (Button) buttonPressed.getSource();
		String command = button.getText().toLowerCase();
		Image image = getImageFromPath(COMMANDS_PATH + command + FORMAT);
		createTimeline(currentUserCommand, image);
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
		cleanField(new User("User4")); // simulo che tocchi all'utente 4
	}

	private void createTimeline(final ImageView currentUser, final Image command) {
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(currentUser.imageProperty(), command)),
				new KeyFrame(Duration.seconds(2), new KeyValue(currentUser.imageProperty(), null)));
		timeline.play();
	}

	@Override
	public void getCommand(final User user, final Command command) {

		Image userCommand = getImageFromPath(COMMANDS_PATH + command.getCommand() + user.getUser() + FORMAT);

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
	 * Method to show which card is pressed by first user and throw it in field.
	 * 
	 * @param clickedCard
	 */
	public void clickedCard(final MouseEvent clickedCard) {
		/* prendo il riferimento alla carta cliccata e ricavo il path */
		ImageView playedCard = (ImageView) clickedCard.getSource();
		@SuppressWarnings("deprecation")
		String path = getCleanPath(playedCard.getImage().impl_getUrl());
		String pathOfImageSelected = getCleanPath(path);
		Image userCommand = getImageFromPath(pathOfImageSelected);

		/* visualizzo la carta in mezzo al campo e tolgo la carta cliccata dalla mano */
		this.user1Field.setImage(userCommand);
		playedCard.setVisible(false);

		/*
		 * CONTROLLER qui chiamo un metodo del controller e gli passo l'indice della
		 * carta selezionata dall'utente e giocata
		 */

		int indexCardSelected = getIndexOfCardSelected(pathOfImageSelected);
		System.out.println(indexCardSelected);

	}

	@Override
	public void getCardsFirstUser(final List<String> firstUserCards) {

		for (int i = 0; i < TOTAL_HAND_CARDS; i++) {

			Image userCard = getImageFromPath(firstUserCards.get(i));
			/* mi salvo le carte in ordine nella mappa */
			indexOfMyCards.put(i, firstUserCards.get(i));

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

	@Override
	public void cleanField(final User user) {

		/*
		 * Prima metto tutti i terreni neri, poi all'utente che deve cominciare lo
		 * imposto giallo
		 */
		Image emptyField = getImageFromPath(EMPTY_FIELD);
		Image emptyFieldMyTurn = getImageFromPath(EMPTY_FIELD_MY_TURN);
		user1Field.setImage(emptyField);
		user2Field.setImage(emptyField);
		user3Field.setImage(emptyField);
		user4Field.setImage(emptyField);

		switch (user.getUser()) {
		case "User1":
			user1Field.setImage(emptyFieldMyTurn);
			break;
		case "User2":
			user2Field.setImage(emptyFieldMyTurn);
			break;
		case "User3":
			user3Field.setImage(emptyFieldMyTurn);
			break;
		case "User4":
			user4Field.setImage(emptyFieldMyTurn);
			break;
		}
	}

	/* Metodo per creare un'immagine dato un path */
	private Image getImageFromPath(final String path) {
		File file = new File(path);
		Image image = new Image(file.toURI().toString());
		return image;
	}

	/* Metodo per recuperare l'indice della carta cliccata */
	private int getIndexOfCardSelected(final String path) {
		int cardIndex = 0;
		for (final Entry<Integer, String> entry : indexOfMyCards.entrySet()) {
			if (path.contains(entry.getValue())) {
				cardIndex = entry.getKey();
			}
		}
		return cardIndex;
	}

	/* Metodo per pulire il path ricavato dalla ImageView */
	private String getCleanPath(final String path) {
		int index = path.indexOf("src");
		String pathOfImageSelected = path.substring(index, path.length());
		return pathOfImageSelected;
	}

}
