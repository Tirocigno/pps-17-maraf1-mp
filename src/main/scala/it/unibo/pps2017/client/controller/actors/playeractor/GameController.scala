
package it.unibo.pps2017.client.controller.actors.playeractor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages.{BriscolaChosen, ClickedCard}
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup, Sword}
import it.unibo.pps2017.core.gui.PlayGameController

import scala.collection.JavaConverters._

 class GameController extends ActorController {

   /** oggetto gui */

   var playGameController: PlayGameController = _
  /**
    * ActorRef of the actor*/
  var currentActorRef: ActorRef = _

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

   //TODO FIX THIS
   def setCommandFromPlayer(command: String): Unit = {
     //currentActorRef ! ClickedCommand(command, )
   }

   def getOrThrow(actorRef: Option[ActorRef]): ActorRef =
     actorRef.getOrElse(throw new NoSuchElementException(noActorFoundMessage))

  def setPlayedCard(cardIndex: Int): Unit = {
    currentActorRef ! ClickedCard(cardIndex, null)
  }

   override def createActor(actorId: String, actorSystem: ActorSystem) = {
     currentActorRef = actorSystem.actorOf(Props(new PlayerActorClient(this, actorId)))
     println(currentActorRef)
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

   //TODO refactor code according to this method
   override def updateGUI(message: ActorMessage): Unit = message match {
     case BriscolaChosen(seed) => selectedBriscola(seed.asString)
     case _ =>
   }

   def selectedBriscola(briscola: String): Unit = briscola match {
     case Sword.asString => currentActorRef ! BriscolaChosen(Sword)
     case Cup.asString => currentActorRef ! BriscolaChosen(Cup)
     case Coin.asString => currentActorRef ! BriscolaChosen(Coin)
     case Club.asString => currentActorRef ! BriscolaChosen(Club)
     case _ => new IllegalArgumentException()
   }
}
