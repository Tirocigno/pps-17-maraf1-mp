package it.unibo.pps2017.core.playerActor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.core.playerActor.PlayerActor.{ClickedCard, ClickedCommand, BriscolaChosen}

import scala.collection.JavaConverters._


abstract class ClientController {

  /** oggetto gui */
  var playGameController: PlayGameController
  val system = ActorSystem("mySystem")
  /**
    * ActorRef of the actor*/
  val myActor: ActorRef = system.actorOf(Props(new PlayerActor(this, "nic")))
  var myTurn: Boolean = _
  var cardOK: Boolean = _


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

  /** Metodo per inviare al PlayerActor il comando cliccato dalla gui */
  def setCommandFromPlayer(command: String): Unit = {
    myActor ! ClickedCommand(command, null)
  }


  def selectedBriscola(briscola: String): Unit = {
    val seed: Seed = null
    seed.setSeed(briscola)
    myActor ! BriscolaChosen(seed)
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

  def setCardOK(cardOK: Boolean): Unit = {
    this.cardOK = cardOK
  }

  def isCardOK(): Boolean = {
    this.cardOK
  }

  def sendPlayersList(playersList: List[String]): Unit = {
    playGameController.setPlayersList(playersList.asJava)
  }


}
