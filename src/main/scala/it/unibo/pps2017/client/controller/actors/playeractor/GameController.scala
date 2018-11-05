package it.unibo.pps2017.client.controller.actors.playeractor

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.passiveactors.{ReplayActor, ViewerActor}
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient
import it.unibo.pps2017.client.view.GuiStack
import it.unibo.pps2017.client.view.game.GameGUIController
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup, Sword}
import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.server.model.Game

import scala.collection.JavaConverters._

class GameController (val clientControllerRef: ClientController) extends MatchController {

  var playGameController: PlayGameController = _
  val clientController: ClientController = clientControllerRef
  var currentActorRef: ActorRef = _
  var myTurn: Boolean = false
  var amIWinner: Boolean = false

  /**
    * Method to create a new PlayerActorClient.
    *
    * @param actorId     Id of PlayerActorClient.
    * @param actorSystem System.
    */
  override def createActor(actorId: String, actorSystem: ActorSystem): Unit = {
    currentActorRef = actorSystem.actorOf(Props(new PlayerActorClient(this, actorId)))
  }

  /**
    * Method to create a new ReplayActor.
    *
    * @param actorId     Id of ReplayActor.
    * @param actorSystem System.
    * @param game        Game from database to review.
    *
    */
  def createReplayActor(actorId: String, actorSystem: ActorSystem, game: Game): Unit = {
    actorSystem.actorOf(Props(new ReplayActor(this, actorId, game)))
  }

  /**
    * Method to create a new ViewerActor.
    *
    * @param actorId     Id of ViewerActor.
    * @param actorSystem System.
    */
  def createViewerActor(actorId: String, actorSystem: ActorSystem): Unit = {
    currentActorRef = actorSystem.actorOf(Props(new ViewerActor(this, actorId)))
  }

  /**
    * Method to access the inner value of the Option, unless there is None. If None, we get the argument back.
    *
    * @param actorRef actor's ref.
    * @return Inner value of the Option.
    */
  def getOrThrow(actorRef: Option[ActorRef]): ActorRef = actorRef.getOrElse(throw new NoSuchElementException(noActorFoundMessage))

  /**
    * Method called from actor (Player, Viewer or Replay) to update GUI.
    *
    * @param message Type of message from actor.
    */
  override def updateGUI(message: ActorMessage): Unit = message match {
    case PlayersRef(playersList) => sendPlayersList(playersList.toList)
    case DistributedCard(cards, _) => sendCardsFirstPlayer(cards)
    case SelectBriscola(_) => selectBriscola()
    case NotifyBriscolaChosen(seed) => sendBriscolaChosen(briscola = seed.asString)
    case CardOk(correctClickedCard, _) => setCardOK(correctClickedCard)
    case NotifyCommandChosen(command, player) => sendCommand(player, command)
    case PlayedCard(card, player) => showPlayersPlayedCard(card, player)
    case Turn(player, endPartialTurn, isFirstPlayer, isReplay) => setCurrentPlayer(player, endPartialTurn, isFirstPlayer, isReplay)
    case ComputeGameScore(player, winner1, winner2, score1, score2, endMatch) => cleanFieldEndTotalTurn(player, winner1, winner2, score1, score2, endMatch)
    case _ =>
  }

  override def setCurrentGui(gui: GameGUIController): Unit = {
    playGameController = gui.asInstanceOf[PlayGameController]
  }

  /**
    * Method to send to PlayerActorClient the clicked command from actual player.
    *
    * @param command Clicked command from actual player.
    */
  def setCommandFromPlayer(command: String): Unit = {
    currentActorRef ! ClickedCommandActualPlayer(command)
  }

  /**
    * Method to send to PlayerActorClient to inform it of played card.
    *
    * @param cardIndex Index of played card.
    */
  def setPlayedCard(cardIndex: Int): Unit = {
    currentActorRef ! ClickedCardActualPlayer(cardIndex)
  }

  /**
    * Method to send to PlayerActorClient his username.
    *
    * @param playerUsername Player's username.
    */
  def setUsernamePlayer(playerUsername: String): Unit = {
    currentActorRef ! SetUsernamePlayer(playerUsername)
  }

  /**
    * Method to send to PlayerActorClient id of match.
    *
    * @param id Match's id.
    */
  def joinPlayerToMatch(id: String): Unit = {
    GuiStack().stage.setOnCloseRequest(_ => {
      this.closedPlayGameView()
      System.exit(0)
    })
    currentActorRef ! IdChannelPublishSubscribe(id)
  }

  /**
    * Method to set the turn.
    *
    * @param turn Boolean turn.
    */
  def setMyTurn(turn: Boolean): Unit = {
    myTurn = turn
  }

  /**
    * Method to send to PlayerActorClient to inform of briscola chosen.
    *
    * @param briscola Briscola chosen from current player.
    */
  def selectedBriscola(briscola: String): Unit = briscola match {
    case Sword.asString => currentActorRef ! BriscolaChosen(Sword)
    case Cup.asString => currentActorRef ! BriscolaChosen(Cup)
    case Coin.asString => currentActorRef ! BriscolaChosen(Coin)
    case Club.asString => currentActorRef ! BriscolaChosen(Club)
    case _ => new IllegalArgumentException()
  }

  /**
    * Method to send to GUI ten cards of the user.
    *
    * @param cards List of ten cards.
    */
  def sendCardsFirstPlayer(cards: List[String]): Unit = {
    playGameController.getCardsFirstPlayer(cards.asJava)
  }

  /**
    * Method to send to GUI briscola chosen from other player.
    *
    * @param briscola Briscola chosen.
    */
  def sendBriscolaChosen(briscola: String): Unit = {
    playGameController.getBriscolaChosen(briscola)
  }

  /**
    * Method to send to GUI command chosen from other player.
    *
    * @param player  Player who clicks the command.
    * @param command Command clicked by other player.
    */
  def sendCommand(player: String, command: String): Unit = {
    playGameController.getCommand(player, command)
  }

  /**
    * Method to send to GUI path of played card from other player.
    *
    * @param path   Played card's path.
    * @param player Player who played card.
    */
  def showPlayersPlayedCard(path: String, player: String): Unit = {
    playGameController.showPlayersPlayedCard(player, path)
  }

  /**
    * Method to verify if player is a winner of this turn or not.
    *
    * @param user     Current player.
    * @param winner1  First of two winners of this turn/match.
    * @param winner2  Second of two winners of this turn/match.
    * @param score1   Aggregated score of first team.
    * @param score2   Aggregated score of second team.
    * @param endMatch True if match is ended, false otherwise.
    */
  def cleanFieldEndTotalTurn(user: String, winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean): Unit = {

    if (score1 == score2) playGameController cleanFieldEndTotalTurn(score1, score2, endMatch) else {
      if (user.equals(winner1) | user.equals(winner2)) {
        if (score1 > score2) playGameController cleanFieldEndTotalTurn(score1, score2, endMatch) else playGameController cleanFieldEndTotalTurn(score2, score1, endMatch)
        this.setWinner(endMatch)
      } else {
        if (score1 > score2) playGameController cleanFieldEndTotalTurn(score2, score1, endMatch) else playGameController cleanFieldEndTotalTurn(score1, score2, endMatch)
      }
    }
  }

  /**
    * Method to send to GUI the player that now will be plays a card.
    *
    * @param player             Player that will be plays a card.
    * @param partialTurnIsEnded Boolean to know if turn is ended.
    * @param isFirstPlayer      Boolean to know if the player is the first of the turn (for show or hide commands)
    */
  def setCurrentPlayer(player: String, partialTurnIsEnded: Boolean, isFirstPlayer: Boolean, isReplay: Boolean): Unit = {
    playGameController.setCurrentPlayer(player, partialTurnIsEnded, isFirstPlayer, isReplay)
  }

  /**
    * Method to send to GUI the command to choose briscola.
    */
  def selectBriscola(): Unit = {
    playGameController.showBriscolaCommands()
  }

  /**
    * Method to send to GUI to understand if clicked card is ok or not.
    *
    * @param cardOK Boolean to know if clicked card is ok or not.
    */
  def setCardOK(cardOK: Boolean): Unit = {
    if (cardOK) playGameController.showPlayedCardOk()
    else playGameController.showPlayedCardError()
  }

  /**
    * Method to send to GUI four players of the match.
    *
    * @param playersList layers' list of match.
    */
  def sendPlayersList(playersList: List[String]): Unit = {
    playGameController.setPlayersList(playersList.asJava)
  }

  /**
    * Method to stop actor and communicated it at controller.
    */
  def endedMatch(): Unit = {
    clientController.notifyGameFinished()
    if (currentActorRef != null) currentActorRef ! PoisonPill
  }

  /**
    * Method to notify actor that view was closed.
    */
  def closedPlayGameView(): Unit = {
    currentActorRef ! NotifyClosedPlayGameView()
  }

  /**
    * Method to set variable amIWinner.
    *
    * @param winner Boolean variable.
    */
  def setWinner(winner: Boolean): Unit = {
    this.amIWinner = winner
  }

  /**
    * Method to know if the current player is the winner.
    *
    * @return getWinner Boolean variable.
    */
  def getWinner: Boolean = {
    this.amIWinner
  }

  /**
    * Method to know if is the turn of actual player.
    *
    * @return myTurn
    */
  def isMyTurn: Boolean = {
    myTurn
  }

}