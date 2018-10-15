
package it.unibo.pps2017.client.controller.actors.playeractor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup, Sword}
import it.unibo.pps2017.core.gui.PlayGameController

import scala.collection.JavaConverters._

class GameController extends ActorController {


  /** oggetto gui */
  var playGameController: PlayGameController = _
  val system = ActorSystem("mySystem")
  /**
    * ActorRef of the actor*/
  var currentActorRef: ActorRef = _

  var myTurn: Boolean = false
  var amIWinner: Boolean = false

  /**
    * Method to send to GUI ten cards of the user.
    *
    * @param cards
    * List of ten cards.
    */
  def sendCardsFirstPlayer(cards: List[String]): Unit = {
    playGameController.getCardsFirstPlayer(cards.asJava)
  }

  /**
    * Method to send to GUI briscola chosen from other player.
    *
    * @param briscola
    * Briscola chosen.
    */
  def sendBriscolaChosen(briscola: String): Unit = {
    playGameController.getBriscolaChosen(briscola)
  }

  /**
    * Method to send to GUI command chosen from other player.
    *
    * @param player
    * Player who clicks the command.
    * @param command
    * Command clicked by other player.
    */
  def sendCommand(player: String, command: String): Unit = {
    playGameController.getCommand(player, command)
  }

  /**
    * Method to send to GUI path of played card from other player.
    *
    * @param path
    * Played card's path.
    * @param player
    * Player who played card.
    */
  def showOtherPlayersPlayedCard(path: String, player: String): Unit = {
    playGameController.showOtherPlayersPlayedCard(player, path)
  }

  /**
    * Method to send to GUI the score of two team.
    *
    * @param firstTeamScore
    * Score of first team.
    * @param secondTeamScore
    * Score of second team.
    * @param endMatch
    * Boolean to know if match is ended.
    */
  def cleanFieldEndTotalTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean): Unit = {
    playGameController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
  }

  /**
    * Method to send to GUI the player that now will be plays a card.
    *
    * @param player
    * Player that will be plays a card.
    * @param partialTurnIsEnded
    * Boolean to know if turn is ended.
    * @param isFirstPlayer
    * Boolean to know if the player is the first of the turn (for show or hide commands)
    */
  def setCurrentPlayer(player: String, partialTurnIsEnded: Boolean, isFirstPlayer: Boolean): Unit = {
    playGameController.setCurrentPlayer(player, partialTurnIsEnded, isFirstPlayer)
  }

  /**
    * Method to send to GUI the command to choose briscola.
    */
  def selectBriscola(): Unit = {
    playGameController.showBriscolaCommands()
  }

  /**
    * Method to send to GUI the current value of timer.
    *
    * @param timer
    * Value of timer.
    */
  def setTimer(timer: Int): Unit = {
    playGameController.setTimer(timer)
  }

  /**
    * Method to send to the actor the clicked command from actual player.
    *
    * @param command
    * Clicked command from actual player.
    */
  def setCommandFromPlayer(command: String): Unit = {
    currentActorRef ! ClickedCommand(command, null)
  }

  def getOrThrow(actorRef: Option[ActorRef]): ActorRef =
    actorRef.getOrElse(throw new NoSuchElementException(noActorFoundMessage))

  /**
    * Method to send to actor to informe of played card.
    *
    * @param cardIndex
    * Index of played card.
    */
  def setPlayedCard(cardIndex: Int): Unit = {
    currentActorRef ! ClickedCard(cardIndex, null)
  }

  /**
    * Method to create a new PlayerActorClient.
    *
    * @param actorId
    * Id of PlayerActorClient.
    * @param actorSystem
    * System.
    */
  override def createActor(actorId: String, actorSystem: ActorSystem): Unit = {
    currentActorRef = actorSystem.actorOf(Props(new PlayerActorClient(this, actorId)))
  }

  /**
    * Method to know if is the turn of actual player.
    *
    * @return
    * myTurn
    */
  def isMyTurn: Boolean = {
    myTurn
  }

  /**
    * Method to set the turn.
    *
    * @param turn
    * Boolean turn.
    */
  def setMyTurn(turn: Boolean): Unit = {
    myTurn = turn
  }

  /**
    * Method to send to GUI to understand if clicked card is ok or not.
    *
    * @param cardOK
    * Boolean to know if clicked card is ok or not.
    */
  def setCardOK(cardOK: Boolean): Unit =
    if (cardOK) playGameController.showPlayedCardOk() else playGameController.showPlayedCardError()

  /**
    * Method to send to GUI four players of the match.
    *
    * @param playersList
    * Players' list of match.
    */
  def sendPlayersList(playersList: List[String]): Unit = {
    playGameController.setPlayersList(playersList.asJava)
  }

  /**
    * Method to set variable amIWinner.
    *
    * @param winner
    * Boolean variable.
    */
  def setWinner(winner: Boolean): Unit = {
    this.amIWinner = winner
  }

  /**
    * Method to know if the current player is the winner.
    *
    * @return
    * getWinner Boolean variable.
    */
  def getWinner: Boolean = {
    this.amIWinner
  }

  /**
    * Method called from PlayerActorClient to update GUI.
    *
    * @param message
    * Type of message from PlayerActorClient.
    */
  override def updateGUI(message: ActorMessage): Unit = message match {
    case PlayersRef(playersList) => sendPlayersList(playersList.toList)
    case DistributedCard(cards, _) => sendCardsFirstPlayer(cards)
    case SelectBriscola(_) => selectBriscola()
    case NotifyBriscolaChosen(seed) => sendBriscolaChosen(briscola = seed.asString)
    case CardOk(correctClickedCard) => setCardOK(correctClickedCard)
    case NotifyCommandChosen(command, player) => sendCommand(player, command)
    case ForcedCardPlayed(card, player) => showOtherPlayersPlayedCard(card, player = player)
    case SetTimer(timer) => setTimer(timer)
    case PlayedCard(card, player) => showOtherPlayersPlayedCard(card, player)
    case Turn(player, endPartialTurn, isFirstPlayer) => setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
    case _ =>
  }

  /**
    * Method to send to actor to inform of briscola chosen.
    *
    * @param briscola
    * Briscola chosen from current player.
    */
  def selectedBriscola(briscola: String): Unit = briscola match {
    case Sword.asString => currentActorRef ! BriscolaChosen(Sword)
    case Cup.asString => currentActorRef ! BriscolaChosen(Cup)
    case Coin.asString => currentActorRef ! BriscolaChosen(Coin)
    case Club.asString => currentActorRef ! BriscolaChosen(Club)
    case _ => new IllegalArgumentException()
  }
}
