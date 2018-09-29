package it.unibo.pps2017.core.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	private static final String START_PATH = "src";
	private static final int TOTAL_HAND_CARDS = 10;

	@FXML
	ImageView wallpaper = new ImageView();

	@FXML
	ImageView firstCard = new ImageView(), secondCard = new ImageView(), thirdCard = new ImageView(),
			fourthCard = new ImageView(), fifthCard = new ImageView(), sixthCard = new ImageView(),
			seventhCard = new ImageView(), eighthCard = new ImageView(), ninthCard = new ImageView(),
			tenthCard = new ImageView();

	@FXML
	ImageView firstCardUser2 = new ImageView(), secondCardUser2 = new ImageView(), thirdCardUser2 = new ImageView(),
			fourthCardUser2 = new ImageView(), fifthCardUser2 = new ImageView(), sixthCardUser2 = new ImageView(),
			seventhCardUser2 = new ImageView(), eighthCardUser2 = new ImageView(), ninthCardUser2 = new ImageView(),
			tenthCardUser2 = new ImageView();

	@FXML
	ImageView firstCardUser3 = new ImageView(), secondCardUser3 = new ImageView(), thirdCardUser3 = new ImageView(),
			fourthCardUser3 = new ImageView(), fifthCardUser3 = new ImageView(), sixthCardUser3 = new ImageView(),
			seventhCardUser3 = new ImageView(), eighthCardUser3 = new ImageView(), ninthCardUser3 = new ImageView(),
			tenthCardUser3 = new ImageView();

	@FXML
	ImageView firstCardUser4 = new ImageView(), secondCardUser4 = new ImageView(), thirdCardUser4 = new ImageView(),
			fourthCardUser4 = new ImageView(), fifthCardUser4 = new ImageView(), sixthCardUser4 = new ImageView(),
			seventhCardUser4 = new ImageView(), eighthCardUser4 = new ImageView(), ninthCardUser4 = new ImageView(),
			tenthCardUser4 = new ImageView();

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

	private List<ImageView> cardsUser2;
	private List<ImageView> cardsUser3;
	private List<ImageView> cardsUser4;

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
		this.cardsUser2 = new ArrayList<>();
		this.cardsUser3 = new ArrayList<>();
		this.cardsUser4 = new ArrayList<>();

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
	}

	/*
	 * metodo temporaneo che eliminero' quando ricevero' la lista delle carte dal
	 * controller
	 */
	public void distributedCards(final ActionEvent buttonPressed) throws InterruptedException {
		getCardsFirstUser(firstUserCards);
		cleanField(new User("User1")); // simulo che tocchi all'utente 1
	}

	private void createTimeline(final ImageView currentUser, final Image command) {
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(currentUser.imageProperty(), command)),
				new KeyFrame(Duration.seconds(2), new KeyValue(currentUser.imageProperty(), null)));
		timeline.play();
	}

	@Override
	public void getCommand(final User user, final Command command) {

		/* CONTROLLER CHE ME LO CHIAMA */
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
		 * CONTROLLER DA CHIAMARE: qui chiamo un metodo del controller e gli passo
		 * l'indice della carta selezionata dall'utente e giocata
		 */

		int indexCardSelected = getIndexOfCardSelected(pathOfImageSelected);
		System.out.println(indexCardSelected);

	}

	@Override
	public void getCardsFirstUser(final List<String> firstUserCards) {

		/* CONTROLLER CHE ME LO CHIAMA */
		initializePlayersHand();
		initializeCommands();

		for (int cardIndex = 0; cardIndex < TOTAL_HAND_CARDS; cardIndex++) {

			Image userCard = getImageFromPath(firstUserCards.get(cardIndex));
			/* mi salvo le carte in ordine nella mappa */
			indexOfMyCards.put(cardIndex, firstUserCards.get(cardIndex));

			switch (cardIndex) {
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

		/* CONTROLLER CHE ME LO CHIAMA */
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
	
	@Override
	public void setCurrentPlayer(final User user) {
		/* CONTROLLER CHE ME LO CHIAMA */
		Image emptyFieldMyTurn = getImageFromPath(EMPTY_FIELD_MY_TURN);
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
		int index = path.indexOf(START_PATH);
		String pathOfImageSelected = path.substring(index, path.length());
		return pathOfImageSelected;
	}

	@Override
	public void showOtherPlayersPlayedCard(final User user, final String cardPath) {

		Image cardPlayed = getImageFromPath(cardPath);

		switch (user.getUser()) {
		case "User2":
			user2Field.setImage(cardPlayed);
			break;
		case "User3":
			user3Field.setImage(cardPlayed);
			break;
		case "User4":
			user4Field.setImage(cardPlayed);
			break;
		}

		/* dopo aver mostrato la carta ne devo eliminare una dalla mano dell'utente */
		deleteCardFromHand(user);

	}

	private void deleteCardFromHand(final User user) {
		switch (user.getUser()) {
		case "User2":
			deleteCard(cardsUser2);
			break;
		case "User3":
			deleteCard(cardsUser3);
			break;
		case "User4":
			deleteCard(cardsUser4);
			break;
		}
	}

	private void deleteCard(final List<ImageView> cardsUser) {
		if (cardsUser.size() - 1 >= 0) {
			cardsUser.get(cardsUser.size() - 1).setVisible(false);
			cardsUser.remove(cardsUser.size() - 1);
		}
	}

	/* Inizializzo le liste con tutte le carte degli altri utenti e poi le mostro */

	private void initializePlayersHand() {
		createCardsListUser2();
		createCardsListUser3();
		createCardsListUser4();
	}

	private void createCardsListUser2() {
		this.cardsUser2.add(firstCardUser2);
		this.cardsUser2.add(secondCardUser2);
		this.cardsUser2.add(thirdCardUser2);
		this.cardsUser2.add(fourthCardUser2);
		this.cardsUser2.add(fifthCardUser2);
		this.cardsUser2.add(sixthCardUser2);
		this.cardsUser2.add(seventhCardUser2);
		this.cardsUser2.add(eighthCardUser2);
		this.cardsUser2.add(ninthCardUser2);
		this.cardsUser2.add(tenthCardUser2);
		this.showOtherPlayersHand(cardsUser2);
	}

	private void createCardsListUser3() {
		this.cardsUser3.add(firstCardUser3);
		this.cardsUser3.add(secondCardUser3);
		this.cardsUser3.add(thirdCardUser3);
		this.cardsUser3.add(fourthCardUser3);
		this.cardsUser3.add(fifthCardUser3);
		this.cardsUser3.add(sixthCardUser3);
		this.cardsUser3.add(seventhCardUser3);
		this.cardsUser3.add(eighthCardUser3);
		this.cardsUser3.add(ninthCardUser3);
		this.cardsUser3.add(tenthCardUser3);
		this.showOtherPlayersHand(cardsUser3);
	}

	private void createCardsListUser4() {
		this.cardsUser4.add(firstCardUser4);
		this.cardsUser4.add(secondCardUser4);
		this.cardsUser4.add(thirdCardUser4);
		this.cardsUser4.add(fourthCardUser4);
		this.cardsUser4.add(fifthCardUser4);
		this.cardsUser4.add(sixthCardUser4);
		this.cardsUser4.add(seventhCardUser4);
		this.cardsUser4.add(eighthCardUser4);
		this.cardsUser4.add(ninthCardUser4);
		this.cardsUser4.add(tenthCardUser4);
		this.showOtherPlayersHand(cardsUser4);
	}

	private void showOtherPlayersHand(final List<ImageView> playerHand) {
		for (final ImageView playerCard : playerHand) {
			playerCard.setVisible(true);
		}
	}

	private void initializeCommands() {
		bussoButton.setVisible(true);
		striscioButton.setVisible(true);
		voloButton.setVisible(true);
	}

	
}
