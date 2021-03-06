package it.unibo.pps2017.core.gui;

import it.unibo.pps2017.client.controller.Controller;
import it.unibo.pps2017.client.controller.actors.playeractor.GameController;
import it.unibo.pps2017.client.view.game.GameGUIController;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayGameController extends GameGUIController implements PlayGame {

  @FXML
  ImageView wallpaper = new ImageView();

  @FXML
  ImageView firstCard = new ImageView(), secondCard = new ImageView(),
      thirdCard = new ImageView(), fourthCard = new ImageView(),
      fifthCard = new ImageView(), sixthCard = new ImageView(),
      seventhCard = new ImageView(), eighthCard = new ImageView(),
      ninthCard = new ImageView(), tenthCard = new ImageView();

  @FXML
  ImageView firstCardUser2 = new ImageView(), secondCardUser2 = new ImageView(),
      thirdCardUser2 = new ImageView(), fourthCardUser2 = new ImageView(),
      fifthCardUser2 = new ImageView(), sixthCardUser2 = new ImageView(),
      seventhCardUser2 = new ImageView(), eighthCardUser2 = new ImageView(),
      ninthCardUser2 = new ImageView(), tenthCardUser2 = new ImageView();

  @FXML
  ImageView firstCardUser3 = new ImageView(), secondCardUser3 = new ImageView(),
      thirdCardUser3 = new ImageView(), fourthCardUser3 = new ImageView(),
      fifthCardUser3 = new ImageView(), sixthCardUser3 = new ImageView(),
      seventhCardUser3 = new ImageView(), eighthCardUser3 = new ImageView(),
      ninthCardUser3 = new ImageView(), tenthCardUser3 = new ImageView();

  @FXML
  ImageView firstCardUser4 = new ImageView(), secondCardUser4 = new ImageView(),
      thirdCardUser4 = new ImageView(), fourthCardUser4 = new ImageView(),
      fifthCardUser4 = new ImageView(), sixthCardUser4 = new ImageView(),
      seventhCardUser4 = new ImageView(), eighthCardUser4 = new ImageView(),
      ninthCardUser4 = new ImageView(), tenthCardUser4 = new ImageView();

  @FXML
  Button bussoButton, voloButton, striscioButton;

  @FXML
  Button coinButton, cupButton, clubButton, swordButton;

  @FXML
  ImageView currentUserCommand, userTwoCommand, userThreeCommand, userFourCommand;

  @FXML
  ImageView user1Field, user2Field, user3Field, user4Field;

  @FXML
  ImageView gameOverImage = new ImageView();

  @FXML
  Label scoreTeams;

  @FXML
  Text briscolaLabel, cardNotOk, usernamePlayer1, usernamePlayer2,
      usernamePlayer3, usernamePlayer4, waitingTime, firstTeamScore, secondTeamScore;

  private GameController gameController;
  private Map<String, String> indexOfMyCards;
  private List<String> playersList;
  private List<ImageView> cardsPlayer1;
  private List<ImageView> cardsPlayer2;
  private List<ImageView> cardsPlayer3;
  private List<ImageView> cardsPlayer4;
  private List<ImageView> editableCardsPlayer2;
  private List<ImageView> editableCardsPlayer3;
  private List<ImageView> editableCardsPlayer4;
  private String player1;
  private String player2;
  private String player3;
  private String player4;
  private List<String> idUserCards;
  private boolean briscolaChosen = true;

  /**
   * PlayGameController constructor.
   */
  public PlayGameController() {
    this.indexOfMyCards = new LinkedHashMap<>();
    this.cardsPlayer1 = new ArrayList<>();
    this.cardsPlayer2 = new ArrayList<>();
    this.cardsPlayer3 = new ArrayList<>();
    this.cardsPlayer4 = new ArrayList<>();
    this.playersList = new ArrayList<>();
    this.idUserCards = new ArrayList<>();
    this.createListWithCardsId();
  }

  @FXML
  public void initialize() {
    createCardsListPlayers();
    this.waitingTime.setVisible(true);
  }

  @Override
  public void setPlayersList(final List<String> playersList) {
    this.waitingTime.setVisible(false);
    this.playersList = playersList;
    this.player1 = playersList.get(0);
    this.player2 = playersList.get(1);
    this.player3 = playersList.get(2);
    this.player4 = playersList.get(3);
    Platform.runLater(() -> {
      usernamePlayer1.setText(this.player1);
      usernamePlayer2.setText(this.player2);
      usernamePlayer3.setText(this.player3);
      usernamePlayer4.setText(this.player4);
    });
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
    Image image = getImageFromPath(PlayGameViewUtils.getCommandsPath()
        + command + PlayGameViewUtils.getFormat());
    createTimeline(currentUserCommand, image);
    gameController.setCommandFromPlayer(command);
  }

  @Override
  public void getCommand(final String player, final String command) {
    Image userCommand = getImageFromPath(PlayGameViewUtils.getCommandsPath()
        + command + PlayGameViewUtils.getFormat());
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
    this.briscolaLabel.setText(PlayGameViewUtils.getBriscolaChosen() + briscola);
    this.briscolaLabel.setVisible(true);
  }

  /**
   * Method to show which card is pressed by first.
   *
   * @param clickedCard clickedCard.
   */
  public void clickedCard(final MouseEvent clickedCard) {
    ImageView playedCard;
    if (gameController.isMyTurn() && briscolaChosen) {
      playedCard = (ImageView) clickedCard.getSource();
      String clickedCardId = playedCard.getId();
      int indexCardSelected = getIndexOfCardSelected(clickedCardId);
      gameController.setPlayedCard(indexCardSelected);
    }
  }

  @Override
  public void showPlayedCardOk() {
    this.cardNotOk.setVisible(false);
    hideCommands();
  }

  @Override
  public void showPlayedCardError() {
    this.cardNotOk.setVisible(true);
  }

  @Override
  public void getCardsFirstPlayer(final List<String> firstUserCards) {
    initializePlayersHand(firstUserCards.size());
    cleanField();
    this.indexOfMyCards.clear();
    int cardCounter = 0;

    for (final ImageView firstPlayerCard : cardsPlayer1) {
      try {
        Image userCard = getImageFromPath(firstUserCards.get(cardCounter));
        indexOfMyCards.put(idUserCards.get(cardCounter), firstUserCards.get(cardCounter));
        firstPlayerCard.setImage(userCard);
        firstPlayerCard.setVisible(true);
        cardCounter++;
      } catch (IndexOutOfBoundsException ignored) { }
    }
  }

  @Override
  public void showPlayersPlayedCard(final String player, final String cardPath) {
    Image cardPlayed = getImageFromPath(cardPath);
    if (player.equals(player1)) {
      this.hidePlayedCard(cardPath);
      this.user1Field.setImage(cardPlayed);
    } else if (player.equals(player2)) {
      this.user2Field.setImage(cardPlayed);
    } else if (player.equals(player3)) {
      this.user3Field.setImage(cardPlayed);
    } else if (player.equals(player4)) {
      this.user4Field.setImage(cardPlayed);
    }
    deleteCardFromHand(player);
  }

  @Override
  public void setCurrentPlayer(final String player, final boolean partialTurnIsEnded,
                               final boolean isFirstPlayer, final boolean isReplay) {

    if (isFirstPlayer && player.equals(playersList.get(0)) && !coinButton.isVisible()) {
      showCommands();
    } else {
      hideCommands();
    }

    if (partialTurnIsEnded) {
      if (!isReplay) {
        try {
          Thread.sleep(PlayGameViewUtils.getSleepCleanField());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      cleanField();
    }

    Image emptyFieldMyTurn = getImageFromPath(PlayGameViewUtils.getEmptyFieldMyTurn());

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
  public void cleanFieldEndTotalTurn(final int actualScoreMyTeam,
                                     final int actualScoreOpponentTeam, boolean endedMatch) {
    cleanField();
    this.briscolaLabel.setVisible(false);
    showScore(actualScoreMyTeam, actualScoreOpponentTeam, endedMatch);
  }

  private void showScore(final int scoreFirstTeam,
                         final int scoreSecondTeam, final boolean endedMatch) {
    Platform.runLater(() -> {
      firstTeamScore.setText(PlayGameViewUtils.getMyTeam() + scoreFirstTeam);
      secondTeamScore.setText(PlayGameViewUtils.getOpponentTeam() + scoreSecondTeam);
      scoreTeams.setText(PlayGameViewUtils.getMyTeamScore()
          + scoreFirstTeam + "\n"
          + PlayGameViewUtils.getOpponentTeamScore()
          + scoreSecondTeam);
      scoreTeams.setVisible(true);
      createLabelScaleTransition(scoreTeams, endedMatch);
    });
  }

  private void createLabelScaleTransition(final Label score, final boolean endedMatch) {
    ScaleTransition scoreTransition =
        new ScaleTransition(Duration.seconds(PlayGameViewUtils.getDurationAnimation()), score);
    scoreTransition.setFromX(PlayGameViewUtils.getStartAnimationPosition());
    scoreTransition.setFromY(PlayGameViewUtils.getStartAnimationPosition());
    scoreTransition.setToX(PlayGameViewUtils.getEndAnimationPosition());
    scoreTransition.setToY(PlayGameViewUtils.getEndAnimationPosition());
    scoreTransition.play();

    scoreTransition.setOnFinished(endScore -> {
      if (endedMatch) {
        Image finalImage;
        boolean winMatch;
        winMatch = gameController.getWinner();
        if (winMatch) {
          finalImage = getImageFromPath(PlayGameViewUtils.getWinMatch());
          createImageScaleTransition(finalImage);
        } else {
          finalImage = getImageFromPath(PlayGameViewUtils.getLoseMatch());
          createImageScaleTransition(finalImage);
        }
      }
      this.scoreTeams.setText("");
    });
  }

  private void createImageScaleTransition(final Image image) {
    this.gameOverImage.setImage(image);
    ScaleTransition scoreTransition =
        new ScaleTransition(Duration.seconds(PlayGameViewUtils.getDurationAnimation()),
            this.gameOverImage);
    scoreTransition.setToX(PlayGameViewUtils.getEndAnimationPosition() + 1);
    scoreTransition.setToY(PlayGameViewUtils.getEndAnimationPosition() + 1);
    scoreTransition.play();
    scoreTransition.setOnFinished(endScore -> {
      this.gameOverImage.setVisible(false);
      this.endedMatch();
    });

  }

  @Override
  public void showBriscolaCommands() {
    this.briscolaChosen = false;
    this.coinButton.setVisible(true);
    this.clubButton.setVisible(true);
    this.cupButton.setVisible(true);
    this.swordButton.setVisible(true);
    this.briscolaLabel.setText(PlayGameViewUtils.getChooseYourBriscola());
    this.briscolaLabel.setVisible(true);
  }

  private void hideBriscolaCommands() {
    this.briscolaChosen = true;
    this.coinButton.setVisible(false);
    this.clubButton.setVisible(false);
    this.cupButton.setVisible(false);
    this.swordButton.setVisible(false);
    this.briscolaLabel.setVisible(false);
  }

  private void createTimeline(final ImageView imageViewToShow, final Image imageCreateFromFile) {
    Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,
        new KeyValue(imageViewToShow.imageProperty(), imageCreateFromFile)),
        new KeyFrame(Duration.seconds(2),
            new KeyValue(imageViewToShow.imageProperty(), null)));
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
    Image emptyField = getImageFromPath(PlayGameViewUtils.getEmptyField());
    this.user1Field.setImage(emptyField);
    this.user2Field.setImage(emptyField);
    this.user3Field.setImage(emptyField);
    this.user4Field.setImage(emptyField);
  }

  private void deleteCardFromHand(final String player) {
    if (player.equals(player2)) {
      deleteCard(editableCardsPlayer2);
    } else if (player.equals(player3)) {
      deleteCard(editableCardsPlayer3);
    } else if (player.equals(player4)) {
      deleteCard(editableCardsPlayer4);
    }
  }

  private void deleteCard(final List<ImageView> cardsUser) {
    if (cardsUser.size() - 1 >= 0) {
      cardsUser.get(cardsUser.size() - 1).setVisible(false);
      cardsUser.remove(cardsUser.size() - 1);
    }
  }

  private void initializePlayersHand(int firstUserCards) {
    this.editableCardsPlayer2 = new ArrayList<>(cardsPlayer2);
    this.editableCardsPlayer3 = new ArrayList<>(cardsPlayer3);
    this.editableCardsPlayer4 = new ArrayList<>(cardsPlayer4);
    this.normalizeHandOtherPlayers(editableCardsPlayer2, firstUserCards);
    this.normalizeHandOtherPlayers(editableCardsPlayer3, firstUserCards);
    this.normalizeHandOtherPlayers(editableCardsPlayer4, firstUserCards);
    this.showOtherPlayersHand(editableCardsPlayer2);
    this.showOtherPlayersHand(editableCardsPlayer3);
    this.showOtherPlayersHand(editableCardsPlayer4);
  }

  private void createCardsListUser1() {
    this.cardsPlayer1.add(firstCard);
    this.cardsPlayer1.add(secondCard);
    this.cardsPlayer1.add(thirdCard);
    this.cardsPlayer1.add(fourthCard);
    this.cardsPlayer1.add(fifthCard);
    this.cardsPlayer1.add(sixthCard);
    this.cardsPlayer1.add(seventhCard);
    this.cardsPlayer1.add(eighthCard);
    this.cardsPlayer1.add(ninthCard);
    this.cardsPlayer1.add(tenthCard);
  }

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
    for (final ImageView image : cardsPlayer2) {
      image.setImage(getImageFromPath(PlayGameViewUtils.getBackReverse()));
    }
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
    for (final ImageView image : cardsPlayer3) {
      image.setImage(getImageFromPath(PlayGameViewUtils.getBack()));
    }
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
    for (final ImageView image : cardsPlayer4) {
      image.setImage(getImageFromPath(PlayGameViewUtils.getBackReverse()));
    }
  }

  private void createCardsListPlayers() {
    createCardsListUser1();
    createCardsListUser2();
    createCardsListUser3();
    createCardsListUser4();
  }

  private Image getImageFromPath(final String path) {
    return new Image(this.getClass().getResourceAsStream(path));
  }

  private int getIndexOfCardSelected(final String clickedCardId) {
    List<String> indexes = new ArrayList<>(indexOfMyCards.keySet());
    return indexes.indexOf(clickedCardId);
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

  private void normalizeHandOtherPlayers(List<ImageView> listOfOtherHand, int cardNotPlayedYet) {
    for (int i = 0; i < PlayGameViewUtils.getMaxCardsInHand() - cardNotPlayedYet; i++) {
      deleteCard(listOfOtherHand);
    }
  }

  private void hidePlayedCard(final String cardPath) {
    String imageClicked = "";
    for (final Map.Entry<String, String> entry : indexOfMyCards.entrySet()) {
      if (cardPath.contains(entry.getValue())) {
        imageClicked = entry.getKey();
      }
    }
    for (final ImageView image : cardsPlayer1) {
      if (image.getId().equals(imageClicked)) {
        image.setVisible(false);
      }
    }
  }

  private void endedMatch() {
    gameController.endedMatch();
  }

  @Override
  public void setController(Controller controller) {
    this.gameController = (GameController) controller;
  }

  @Override
  public void notifyError(Throwable throwable) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION, throwable.getMessage(), ButtonType.OK);
    alert.showAndWait();
  }
}
