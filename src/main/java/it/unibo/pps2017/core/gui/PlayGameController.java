package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.actors.playeractor.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PlayGameController implements PlayGame {

    private static final String COMMANDS_PATH = "src/main/resources/it/unibo/pps2017/core/gui/commands/";
    private static final String EMPTY_FIELD = "src/main/resources/it/unibo/pps2017/core/gui/cards/emptyField.png";
    private static final String EMPTY_FIELD_MY_TURN = "src/main/resources/it/unibo/pps2017/core/gui/cards/emptyFieldMyTurn.png";
    private static final String WIN_MATCH = "src/main/resources/it/unibo/pps2017/core/gui/images/win.png";
    private static final String LOSE_MATCH = "src/main/resources/it/unibo/pps2017/core/gui/images/lose.png";
    private static final int DURATION_ANIMATION = 3;
    private static final int START_ANIMATION_POSITION = 1;
    private static final int END_ANIMATION_POSITION = 2;
    private static final String FORMAT = ".png";
    private static final int TOTAL_HAND_CARDS = 10;
    private GameController gameController;

    @FXML
    ImageView wallpaper = new ImageView();

    @FXML
    ImageView firstCard = new ImageView(), secondCard = new ImageView(), thirdCard = new ImageView(), fourthCard = new ImageView(), fifthCard = new ImageView(), sixthCard = new ImageView(), seventhCard = new ImageView(), eighthCard = new ImageView(), ninthCard = new ImageView(), tenthCard = new ImageView();

    @FXML
    ImageView firstCardUser2 = new ImageView(), secondCardUser2 = new ImageView(), thirdCardUser2 = new ImageView(), fourthCardUser2 = new ImageView(), fifthCardUser2 = new ImageView(), sixthCardUser2 = new ImageView(), seventhCardUser2 = new ImageView(), eighthCardUser2 = new ImageView(), ninthCardUser2 = new ImageView(), tenthCardUser2 = new ImageView();

    @FXML
    ImageView firstCardUser3 = new ImageView(), secondCardUser3 = new ImageView(), thirdCardUser3 = new ImageView(), fourthCardUser3 = new ImageView(), fifthCardUser3 = new ImageView(), sixthCardUser3 = new ImageView(), seventhCardUser3 = new ImageView(), eighthCardUser3 = new ImageView(), ninthCardUser3 = new ImageView(), tenthCardUser3 = new ImageView();

    @FXML
    ImageView firstCardUser4 = new ImageView(), secondCardUser4 = new ImageView(), thirdCardUser4 = new ImageView(), fourthCardUser4 = new ImageView(), fifthCardUser4 = new ImageView(), sixthCardUser4 = new ImageView(), seventhCardUser4 = new ImageView(), eighthCardUser4 = new ImageView(), ninthCardUser4 = new ImageView(), tenthCardUser4 = new ImageView();

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
    Label timer, scoreTeams;

    @FXML
    Text briscolaLabel, cardNotOk;


    private Map<String, String> indexOfMyCards;
    private List<String> playersList;
    private List<ImageView> cardsPlayer2;
    private List<ImageView> cardsPlayer3;
    private List<ImageView> cardsPlayer4;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    /* Due variabili per gestire la carta giocata */
    private String pathOfImageSelected;
    private ImageView playedCard;

    private List<String> idUserCards;

    public PlayGameController() {
        this.indexOfMyCards = new LinkedHashMap<>();
        this.cardsPlayer2 = new ArrayList<>();
        this.cardsPlayer3 = new ArrayList<>();
        this.cardsPlayer4 = new ArrayList<>();
        this.playersList = new ArrayList<>();
        this.idUserCards = new ArrayList<>();
        this.createListWithCardsId();  // creo la lista degli id delle carte del player
    }

    public void setGameController(final GameController controller) {
        this.gameController = controller;
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
        gameController.setCommandFromPlayer(command);
    }

    @Override
    public void getCommand(final String player, final String command) {
        Image userCommand = getImageFromPath(COMMANDS_PATH + command + FORMAT);
        if (player.equals(player2)) {
            createTimeline(userTwoCommand, userCommand);
        } else if (player.equals(player3)) {
            createTimeline(userThreeCommand, userCommand);
        } else if (player.equals(player4)) {
            createTimeline(userFourCommand, userCommand);
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
        gameController.selectedBriscola(briscola);
        hideBriscolaCommands();
        showCommands();
    }

    @Override
    public void getBriscolaChosen(final String briscola) {
        this.briscolaLabel.setText("Briscola chosen: " + briscola);
        this.briscolaLabel.setVisible(true);
    }

    /**
     * Method to show which card is pressed by first.
     *
     * @param clickedCard clickedCard.
     */
    public void clickedCard(final MouseEvent clickedCard) {
        if (gameController.isMyTurn()) {
            this.playedCard = (ImageView) clickedCard.getSource();
            String clickedCardId = playedCard.getId();
            this.pathOfImageSelected = getPathFromMap(clickedCardId);
            int indexCardSelected = getIndexOfCardSelected(clickedCardId);
            gameController.setPlayedCard(indexCardSelected);
        }
    }

    @Override
    public void showPlayedCardOk() {
        this.cardNotOk.setVisible(false);
        Image imagePlayedCard = getImageFromPath(pathOfImageSelected);
        this.user1Field.setImage(imagePlayedCard);
        playedCard.setVisible(false);
        hideCommands();
    }

    @Override
    public void showPlayedCardError() {
        this.cardNotOk.setVisible(true);
    }


    @Override
    public void getCardsFirstPlayer(final List<String> firstUserCards) {

        initializePlayersHand(); // mostro il retro delle carte degli altri giocatori
        this.indexOfMyCards.clear(); // svuoto la mappa per i turni successivi

        for (int cardIndex = 0; cardIndex < TOTAL_HAND_CARDS; cardIndex++) {
            Image userCard = getImageFromPath(firstUserCards.get(cardIndex));
            /* mi salvo le carte in ordine nella mappa */
            indexOfMyCards.put(idUserCards.get(cardIndex), firstUserCards.get(cardIndex));

            System.out.println(firstUserCards.get(cardIndex));
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

    public void setForcedPlayedCard(final String card) {
        //Image cardPlayed = getImageFromPath(card);
        //this.user1Field.setImage(cardPlayed);
    }

    @Override
    public void showOtherPlayersPlayedCard(final String player, final String cardPath) {
        Image cardPlayed = getImageFromPath(cardPath);

        if (player.equals(player2)) {
            this.user2Field.setImage(cardPlayed);
        } else if (player.equals(player3)) {
            this.user3Field.setImage(cardPlayed);
        } else if (player.equals(player4)) {
            this.user4Field.setImage(cardPlayed);
        }
        /* dopo aver mostrato la carta ne devo eliminare una dalla mano dell'utente */
        deleteCardFromHand(player);
    }

    @Override
    public void setCurrentPlayer(final String player, final boolean partialTurnIsEnded, final boolean isFirstPlayer) {

        if (isFirstPlayer && player.equals(playersList.get(0)) && !coinButton.isVisible()) {
            showCommands();
        } else {
            hideCommands();
        }

        /*if (partialTurnIsEnded) {
            cleanField();
        }*/
        if(isFirstPlayer) cleanField();

        Image emptyFieldMyTurn = getImageFromPath(EMPTY_FIELD_MY_TURN);

        if (player.equals(player1)) {
            this.user1Field.setImage(emptyFieldMyTurn);
        } else if (player.equals(player2)) {
            this.user2Field.setImage(emptyFieldMyTurn);
        } else if (player.equals(player3)) {
            this.user3Field.setImage(emptyFieldMyTurn);
        } else if (player.equals(player4)) {
            this.user4Field.setImage(emptyFieldMyTurn);
        }
    }

    @Override
    public void cleanFieldEndTotalTurn(final int actualScoreMyTeam, final int actualScoreOpponentTeam, boolean endedMatch) {
        cleanField();
        this.briscolaLabel.setVisible(false);
        showScore(actualScoreMyTeam, actualScoreOpponentTeam, endedMatch);
    }

    @Override
    public void setTimer(final int timer) {
        String newTimer = Integer.toString(timer);
        this.timer.setText(newTimer);
    }

    private void showScore(final int scoreFirstTeam, final int scoreSecondTeam, final boolean endedMatch) {
        Platform.runLater( () -> {
            scoreTeams.setText("My team's score: " + scoreFirstTeam + "\nOpponent team's score: " + scoreSecondTeam);
            scoreTeams.setVisible(true);
            createLabelScaleTransition(scoreTeams, endedMatch);
        });
    }

    private void createLabelScaleTransition(final Label score, final boolean endedMatch) {
        ScaleTransition scoreTransition = new ScaleTransition(Duration.seconds(DURATION_ANIMATION), score);
        scoreTransition.setFromX(START_ANIMATION_POSITION);
        scoreTransition.setFromY(START_ANIMATION_POSITION);
        scoreTransition.setToX(END_ANIMATION_POSITION);
        scoreTransition.setToY(END_ANIMATION_POSITION);
        scoreTransition.play();

        scoreTransition.setOnFinished(endScore -> {
            if (endedMatch) {
                Image finalImage;
                boolean winMatch;
                winMatch = gameController.getWinner();
                if (winMatch) {
                    finalImage = getImageFromPath(WIN_MATCH);
                    createImageScaleTransition(finalImage);
                } else {
                    finalImage = getImageFromPath(LOSE_MATCH);
                    createImageScaleTransition(finalImage);
                }
            }
            this.scoreTeams.setText("");
        });
    }

    private void createImageScaleTransition(final Image image) {
        this.gameOverImage.setImage(image);
        ScaleTransition scoreTransition = new ScaleTransition(Duration.seconds(DURATION_ANIMATION), this.gameOverImage);
        scoreTransition.setToX(END_ANIMATION_POSITION + 1);
        scoreTransition.setToY(END_ANIMATION_POSITION + 1);
        scoreTransition.play();
        scoreTransition.setOnFinished(endScore -> this.gameOverImage.setVisible(false));
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
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(imageViewToShow.imageProperty(), imageCreateFromFile)), new KeyFrame(Duration.seconds(2), new KeyValue(imageViewToShow.imageProperty(), null)));
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
        if (player.equals(player2)) {
            deleteCard(cardsPlayer2);
        } else if (player.equals(player3)) {
            deleteCard(cardsPlayer3);
        } else if (player.equals(player4)) {
            deleteCard(cardsPlayer4);
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

    private int getIndexOfCardSelected(final String clickedCardId) {
        List<String> indexes = new ArrayList<>(indexOfMyCards.keySet());
        return indexes.indexOf(clickedCardId);
    }

    private String getPathFromMap(final String clickedCardId) {
        String correctPath = "";
        for (final Entry<String, String> entry : indexOfMyCards.entrySet()) {
            if (clickedCardId.equals(entry.getKey())) {
                correctPath = entry.getValue();
            }
        }
        return correctPath;
    }

    /**
     * This method sets four players of the game.
     *
     * @param playersList players' list
     */
    public void setPlayersList(final List<String> playersList) {
        this.playersList = playersList;
        this.player1 = playersList.get(0);
        this.player2 = playersList.get(1);
        this.player3 = playersList.get(2);
        this.player4 = playersList.get(3);
    }

    private void createListWithCardsId() {
        this.idUserCards.add("firstCard");
        this.idUserCards.add("secondCard");
        this.idUserCards.add("thirdCard");
        this.idUserCards.add("fourthCard");
        this.idUserCards.add("fifthCard");
        this.idUserCards.add("sixthCard");
        this.idUserCards.add("seventhCard");
        this.idUserCards.add("eighthCard");
        this.idUserCards.add("ninthCard");
        this.idUserCards.add("tenthCard");
    }
}
