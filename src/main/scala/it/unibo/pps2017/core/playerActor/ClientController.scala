package it.unibo.pps2017.core.playerActor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.core.player.Command.Command
import it.unibo.pps2017.core.player._
import it.unibo.pps2017.core.playerActor.PlayerActor.{ClickedCardMsg, ClickedCommandMsg, SelectBriscolaMsg}

import scala.collection.JavaConverters._


 abstract class ClientController {

   /** oggetto gui */
  var playGameController: PlayGameController
   val system = ActorSystem("mySystem")
   /**
     * ActorRef of the actor*/
   val myActor: ActorRef = system.actorOf(Props(new PlayerActor(this)))



  def getCardsFirstPlayer(cards: List[String]): Unit = {
    playGameController.getCardsFirstPlayer(cards.asJava)
  }

  def getBriscolaChosen(briscola: String): Unit = {
    playGameController.getBriscolaChosen(briscola)
  }

  def getCommand(command: Command, player: Player): Unit = {
    playGameController.getCommand(player, command)
  }

  def showOtherPlayersPlayedCard(path: String, player: Player): Unit = {
    playGameController.showOtherPlayersPlayedCard(player, path)
  }

  def cleanFieldEndTotalTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean): Unit = {
    playGameController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
  }

  def setCurrentPlayer(player: Player, partialTurnIsEnded: Boolean, isFirstPlayer: Boolean): Unit = {
   playGameController.setCurrentPlayer(player, partialTurnIsEnded, isFirstPlayer)
  }


   /** Metodo per inviare al PlayerActor il comando cliccato dalla gui */
   def setCommandFromPlayer(command: String, player: Player): Unit = {
    myActor ! ClickedCommandMsg(command, player)
   }

   def selectBriscola(briscola: String): Unit = {
     myActor ! SelectBriscolaMsg(briscola)
   }

   def setPlayedCard(cardIndex: Int): Unit ={
     myActor ! ClickedCardMsg(cardIndex)
   }


}
