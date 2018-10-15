
package it.unibo.pps2017.client.controller.actors.playeractor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient.{BriscolaChosen, ClickedCard, ClickedCommand}
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup, Sword}
import it.unibo.pps2017.core.gui.PlayGameController
import scala.language.implicitConversions
import it.unibo.pps2017.client.controller.actors.playeractor.getOrThrow

import scala.collection.JavaConverters._

 class GameController extends ActorController {

  /** oggetto gui */

  var playGameController: PlayGameController
  val system = ActorSystem("mySystem")
  /**
    * ActorRef of the actor*/
  var myActor: Option[ActorRef] = None

  var myTurn: Boolean = _
  var amIWinner: Boolean = _

  def getCardsFirstPlayer(cards: List[String]): Unit = {
    playGameController.getCardsFirstPlayer(cards.asJava)
  }

  def getBriscolaChosen(briscola: String): Unit = {
    playGameController.getBriscolaChosen(briscola)
  }

  def getCommand(player: String, command: String): Unit = {
    playGameController.getCommand(player, command)
  }

  def showOtherPlayersPlayedCard(path: String, player: String): Unit = {
    playGameController.showOtherPlayersPlayedCard(player, path)
  }

  def cleanFieldEndTotalTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean): Unit = {
    playGameController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
  }

  def setCurrentPlayer(player: String, partialTurnIsEnded: Boolean, isFirstPlayer: Boolean): Unit = {
    playGameController.setCurrentPlayer(player, partialTurnIsEnded, isFirstPlayer)
  }

  def selectBriscola() = {
    playGameController.showBriscolaCommands()
  }

  def setTimer(timer: Int) = {
    playGameController.setTimer(timer)
  }

  def setCommandFromPlayer(command: String): Unit = {
    myActor ! ClickedCommand(command, null)
  }

  def selectedBriscola(briscola: String): Unit = briscola match {
    case Sword.asString => getOrThrow(myActor) ! BriscolaChosen(Sword)
    case Cup.asString => myActor BriscolaChosen(Cup)
    case Coin.asString => myActor ! BriscolaChosen(Coin)
    case Club.asString => myActor ! BriscolaChosen(Club)
    case _ => new IllegalArgumentException()
  }

  def setPlayedCard(cardIndex: Int): Unit = {
    myActor ! ClickedCard(cardIndex, null)
  }

  def isMyTurn(): Boolean = {
    return myTurn
  }

  def setMyTurn(turn: Boolean): Unit = {
    this.myTurn = turn
  }

  def setCardOK(cardOK: Boolean): Unit = cardOK match {
    case true => playGameController.showPlayedCardOk()
    case false => playGameController.showPlayedCardError()
  }

  def sendPlayersList(playersList: List[String]): Unit = {
    playGameController.setPlayersList(playersList.asJava)
  }

  def setWinner(winner: Boolean): Unit = {
    this.amIWinner = winner
  }

  def getWinner(): Boolean = {
    this.amIWinner
  }

   override def createActor(actorId: String, actorSystem: ActorSystem) = {
     myActor = Some (actorSystem.actorOf(Props(new PlayerActorClient(this, actorId))))
   }
}
