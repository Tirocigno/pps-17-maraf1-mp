package it.unibo.pps2017.core.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import it.unibo.pps2017.core.playerActor.ClientController;

public class PlayGameController implements PlayGame {

    private static final String COMMANDS_PATH = "src/main/java/it/unibo/pps2017/core/gui/commands/";
    private static final String EMPTY_FIELD = "src/main/java/it/unibo/pps2017/core/gui/cards/emptyField.png";
    private static final String EMPTY_FIELD_MY_TURN = "src/main/java/it/unibo/pps2017/core/gui/cards/emptyFieldMyTurn.png";
    private static final String WIN_MATCH = "src/main/java/it/unibo/pps2017/core/gui/images/win.png";
    private static final String LOSE_MATCH = "src/main/java/it/unibo/pps2017/core/gui/images/lose.png";
    private static final int DURATION_ANIMATION = 3;
    private static final int START_ANIMATION_POSITION = 1;
    private static final int END_ANIMATION_POSITION = 2;
    private static final String FORMAT = ".png";
    private static final String START_PATH = "src";
    private static final int TOTAL_HAND_CARDS = 10;
    private static final String PLAYER_1 = "Player1";
    private static final String PLAYER_2 = "Player2";
    private static final String PLAYER_3 = "Player3";
    private static final String PLAYER_4 = "Player4";
    private ClientController clientController;

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
    Button buttonStart, bussoButton, voloButton, striscioButton;

    @FXML
    Button coinButton, cupButton, clubButton, swordButton;

    @FXML
    ImageView currentUserCommand, userTwoCommand, userThreeCommand, userFourCommand;

    @FXML
    ImageView user1Field, user2Field, user3Field, user4Field;

    @FXML
    ImageView gameOverImage = new ImageView();

    @FXML
    Label timer, score;

    @FXML
    Text briscolaLabel, cardNotOk;

    //List<ImageView> userCards;

    /*
     * in questa mappa avro' l'indice della carta e il suo path, mi serve per capire
     * quando gioco una carta quale e' e dirlo al controller
     */
    private Map<Integer, String> indexOfMyCards;
    private List<String> firstPlayerCards;
    private List<String> playersList;
    private List<ImageView> cardsPlayer2;
    private List<ImageView> cardsPlayer3;
    private List<ImageView> cardsPlayer4;

    public PlayGameController() {

        /*
         * Simulo il fatto di avere la mia lista di carte. Quando questa mi verra'
         * passata dal controller eliminero' tutto cio'
         */
        this.firstPlayerCards = new ArrayList<>();
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/10Sword.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/9Club.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/8Coin.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/7Cup.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/6Coin.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/5Coin.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/4Coin.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/3Club.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/2Coin.png");
        this.firstPlayerCards.add("src/main/resources/it/unibo/pps2017/cards/1Sword.png");

        this.indexOfMyCards = new HashMap<>();
        this.cardsPlayer2 = new ArrayList<>();
        this.cardsPlayer3 = new ArrayList<>();
        this.cardsPlayer4 = new ArrayList<>();

    }


    /**
     * This method permits to view the command that first user selected.
     * Possibilities: busso, striscio, volo.
     *
     * @param buttonPressed button pressed from principal user
     */
    public void signalMyCommands(final ActionEvent buttonPressed) {
        Button button = (Button) buttonPressed.getSource();
        String command = button.getText().toLowerCase();
        Image image = getImageFromPath(COMMANDS_PATH + command + FORMAT);
        createTimeline(currentUserCommand, image);
        clientController.setCommandFromPlayer(command);
    }


    public void getCommand(final String player, final String command) {

        Image userCommand = getImageFromPath(COMMANDS_PATH + command + FORMAT);

        switch (player) {
            case PLAYER_2:
                createTimeline(userTwoCommand, userCommand);
                break;
            case PLAYER_3:
                createTimeline(userThreeCommand, userCommand);
                break;
            case PLAYER_4:
                createTimeline(userFourCommand, userCommand);
                break;
        }
    }


    /**
     * This method permits to catch briscola selected by player.
     *
     * @param buttonPressed button pressed (cup, sword, club or coin) from principal player
     */
    public void selectBriscola(final ActionEvent buttonPressed) {
        Button button = (Button) buttonPressed.getSource();
        String briscola = button.getText();
        clientController.selectedBriscola(briscola);
        hideBriscolaCommands();
        getBriscolaChosen(briscola);
    }

    @Override
    public void getBriscolaChosen(final String briscola) {
        this.briscolaLabel.setText("Briscola chosen: " + briscola);
        this.briscolaLabel.setVisible(true);
    }
	

	/*
	 * metodo temporaneo che eliminero' quando ricevero' la lista delle carte dal
	 * controller

	public void distributedCards(final ActionEvent buttonPressed) throws InterruptedException {
		getCardsFirstPlayer(firstPlayerCards);
		setCurrentPlayer(new Player("Player1"), false); // simulo che tocchi all'utente 1
	} */


    /**
     * Method to show which card is pressed by first user and throw it in field.
     *
     * @param clickedCard
     */
    public void clickedCard(final MouseEvent clickedCard) {

        this.cardNotOk.setVisible(false);
        if (clientController.isMyTurn()) {
            ImageView playedCard = (ImageView) clickedCard.getSource();
            @SuppressWarnings("deprecation")
            String path = getCleanPath(playedCard.getImage().impl_getUrl());
            String pathOfImageSelected = getCleanPath(path);
            Image imagePlayedCard = getImageFromPath(pathOfImageSelected);

            int indexCardSelected = getIndexOfCardSelected(pathOfImageSelected);
            clientController.setPlayedCard(indexCardSelected);

            if (clientController.cardOK()) {
                /* visualizzo la carta in mezzo al campo e tolgo la carta cliccata dalla mano */
                this.user1Field.setImage(imagePlayedCard);
                playedCard.setVisible(false);
                hideCommands();
            } else {
                this.cardNotOk.setVisible(true);
            }
        }
    }

    @Override
    public void getCardsFirstPlayer(final List<String> firstUserCards) {

        initializePlayersHand(); // mostro il retro delle carte degli altri giocatori

        this.indexOfMyCards.clear(); // svuoto la mappa per i turni successivi

        for (int cardIndex = 0; cardIndex < TOTAL_HAND_CARDS; cardIndex++) {

            Image userCard = getImageFromPath(firstUserCards.get(cardIndex));
            /* mi salvo le carte in ordine nella mappa */
            indexOfMyCards.put(cardIndex, firstUserCards.get(cardIndex));

            switch (cardIndex) {
                case 0:
                    this.firstCard.setImage(userCard);
                case 1:
                    this.secondCard.setImage(userCard);
                case 2:
                    this.thirdCard.setImage(userCard);
                case 3:
                    this.fourthCard.setImage(userCard);
                case 4:
                    this.fifthCard.setImage(userCard);
                case 5:
                    this.sixthCard.setImage(userCard);
                case 6:
                    this.seventhCard.setImage(userCard);
                case 7:
                    this.eighthCard.setImage(userCard);
                case 8:
                    this.ninthCard.setImage(userCard);
                case 9:
                    this.tenthCard.setImage(userCard);
            }
        }
    }

    @Override
    public void showOtherPlayersPlayedCard(final String player, final String cardPath) {

        Image cardPlayed = getImageFromPath(cardPath);

        switch (player) {
            case PLAYER_2:
                this.user2Field.setImage(cardPlayed);
                break;
            case PLAYER_3:
                this.user3Field.setImage(cardPlayed);
                break;
            case PLAYER_4:
                this.user4Field.setImage(cardPlayed);
                break;
        }

        /* dopo aver mostrato la carta ne devo eliminare una dalla mano dell'utente */
        deleteCardFromHand(player);

    }

    @Override
    public void setCurrentPlayer(final String player, final boolean partialTurnIsEnded, final boolean isFirstPlayer) {

        /* se sono il primo ad iniziare il turno mostro i comandi busso, striscio, volo */
        if (isFirstPlayer) {
            showCommands();
        } else {
            hideCommands();
        }

        /* se un giro e' stato fatto, devo eliminare tutte le carte dal campo */
        if (partialTurnIsEnded) {
            cleanField();
        }

        Image emptyFieldMyTurn = getImageFromPath(EMPTY_FIELD_MY_TURN);
        switch (player) {

            case PLAYER_1:
                this.user1Field.setImage(emptyFieldMyTurn);
                break;
            case PLAYER_2:
                this.user2Field.setImage(emptyFieldMyTurn);
                break;
            case PLAYER_3:
                this.user3Field.setImage(emptyFieldMyTurn);
                break;
            case PLAYER_4:
                this.user4Field.setImage(emptyFieldMyTurn);
                break;
        }
    }

    @Override
    public void cleanFieldEndTotalTurn(final int actualScoreMyTeam, final int actualScoreOpponentTeam,
                                       boolean endedMatch) {

        cleanField();
        this.briscolaLabel.setVisible(false); // finito un turno nascondo la label con la briscola perche' verra' riscelta
        showScore(actualScoreMyTeam, actualScoreOpponentTeam, endedMatch);
    }

    private void showScore(final int scoreFirstTeam, final int scoreSecondTeam, final boolean endedMatch) {
        this.score.setText("Score: " + scoreFirstTeam + "-" + scoreSecondTeam);
        this.score.setVisible(true);
        createLabelScaleTransition(this.score, endedMatch);
    }

    private void createLabelScaleTransition(final Label score, final boolean endedMatch) {
        ScaleTransition scoreTransition = new ScaleTransition(Duration.seconds(DURATION_ANIMATION), this.score);
        scoreTransition.setFromX(START_ANIMATION_POSITION);
        scoreTransition.setFromY(START_ANIMATION_POSITION);
        scoreTransition.setToX(END_ANIMATION_POSITION);
        scoreTransition.setToY(END_ANIMATION_POSITION);
        scoreTransition.play();

        scoreTransition.setOnFinished(endScore -> {
            if (endedMatch) {
                Image finalImage;
                boolean winMatch = false; // da sostituire con la funzione del controller
                // chiamo metodo controller che mi dice se ho vinto
                if (winMatch) {
                    finalImage = getImageFromPath(WIN_MATCH);
                    createImageScaleTransition(finalImage);
                } else {
                    finalImage = getImageFromPath(LOSE_MATCH);
                    createImageScaleTransition(finalImage);
                }
            }
            this.score.setText("");
        });
    }

    private void createImageScaleTransition(final Image image) {
        this.gameOverImage.setImage(image);
        ScaleTransition scoreTransition = new ScaleTransition(Duration.seconds(DURATION_ANIMATION), this.gameOverImage);
        scoreTransition.setToX(END_ANIMATION_POSITION + 1);
        scoreTransition.setToY(END_ANIMATION_POSITION + 1);
        scoreTransition.play();
        scoreTransition.setOnFinished(endScore -> {
            this.gameOverImage.setVisible(false);
        });
    }

    public void showBriscolaCommands() {
        this.coinButton.setVisible(true);
        this.clubButton.setVisible(true);
        this.cupButton.setVisible(true);
        this.swordButton.setVisible(true);
        this.briscolaLabel.setText("Choose your briscola");
        this.briscolaLabel.setVisible(true);
    }

    private void hideBriscolaCommands() {
        this.coinButton.setVisible(false);
        this.clubButton.setVisible(false);
        this.cupButton.setVisible(false);
        this.swordButton.setVisible(false);
        this.briscolaLabel.setVisible(false);
    }

    private void createTimeline(final ImageView imageViewToShow, final Image imageCreateFromFile) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(imageViewToShow.imageProperty(), imageCreateFromFile)),
                new KeyFrame(Duration.seconds(2), new KeyValue(imageViewToShow.imageProperty(), null)));
        timeline.play();
    }

    private void showOtherPlayersHand(final List<ImageView> playerHand) {
        for (final ImageView playerCard : playerHand) {
            playerCard.setVisible(true);
        }
    }

    private void showCommands() {
        this.bussoButton.setVisible(true);
        this.striscioButton.setVisible(true);
        this.voloButton.setVisible(true);
    }

    private void hideCommands() {
        this.bussoButton.setVisible(false);
        this.striscioButton.setVisible(false);
        this.voloButton.setVisible(false);
    }

    private void cleanField() {
        Image emptyField = getImageFromPath(EMPTY_FIELD);
        this.user1Field.setImage(emptyField);
        this.user2Field.setImage(emptyField);
        this.user3Field.setImage(emptyField);
        this.user4Field.setImage(emptyField);
    }


    private void deleteCardFromHand(final String player) {
        switch (player) {
            case PLAYER_2:
                deleteCard(cardsPlayer2);
                break;
            case PLAYER_3:
                deleteCard(cardsPlayer3);
                break;
            case PLAYER_4:
                deleteCard(cardsPlayer4);
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

    /* Questi metodi servono per avere una lista delle carte di ogni giocatore,
     * cosi' ogni volta che ne viene giocata una la posso rimuovere e nascondere nella view
     */
    private void createCardsListUser2() {
        this.cardsPlayer2.add(firstCardUser2);
        this.cardsPlayer2.add(secondCardUser2);
        this.cardsPlayer2.add(thirdCardUser2);
        this.cardsPlayer2.add(fourthCardUser2);
        this.cardsPlayer2.add(fifthCardUser2);
        this.cardsPlayer2.add(sixthCardUser2);
        this.cardsPlayer2.add(seventhCardUser2);
        this.cardsPlayer2.add(eighthCardUser2);
        this.cardsPlayer2.add(ninthCardUser2);
        this.cardsPlayer2.add(tenthCardUser2);
        this.showOtherPlayersHand(cardsPlayer2);
    }

    private void createCardsListUser3() {
        this.cardsPlayer3.add(firstCardUser3);
        this.cardsPlayer3.add(secondCardUser3);
        this.cardsPlayer3.add(thirdCardUser3);
        this.cardsPlayer3.add(fourthCardUser3);
        this.cardsPlayer3.add(fifthCardUser3);
        this.cardsPlayer3.add(sixthCardUser3);
        this.cardsPlayer3.add(seventhCardUser3);
        this.cardsPlayer3.add(eighthCardUser3);
        this.cardsPlayer3.add(ninthCardUser3);
        this.cardsPlayer3.add(tenthCardUser3);
        this.showOtherPlayersHand(cardsPlayer3);
    }

    private void createCardsListUser4() {
        this.cardsPlayer4.add(firstCardUser4);
        this.cardsPlayer4.add(secondCardUser4);
        this.cardsPlayer4.add(thirdCardUser4);
        this.cardsPlayer4.add(fourthCardUser4);
        this.cardsPlayer4.add(fifthCardUser4);
        this.cardsPlayer4.add(sixthCardUser4);
        this.cardsPlayer4.add(seventhCardUser4);
        this.cardsPlayer4.add(eighthCardUser4);
        this.cardsPlayer4.add(ninthCardUser4);
        this.cardsPlayer4.add(tenthCardUser4);
        this.showOtherPlayersHand(cardsPlayer4);
    }

    /* Metodo per creare un'immagine dato un path */
    private Image getImageFromPath(final String path) {
        File file = new File(path);
        return new Image(file.toURI().toString());
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


    /**
     * This method sets four players of the game.
     *
     * @param playersList players' list
     */
    public void setPlayersList(final List<String> playersList) {
        this.playersList = playersList;
    }


}
