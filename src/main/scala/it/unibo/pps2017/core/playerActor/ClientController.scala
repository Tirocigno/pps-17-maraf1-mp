package it.unibo.pps2017.core.playerActor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.core.player._
import it.unibo.pps2017.core.playerActor.PlayerActor.{ClickedCard, ClickedCommand}

import scala.collection.JavaConverters._


 abstract class ClientController {

   /** oggetto gui */
  var playGameController: PlayGameController
   val system = ActorSystem("mySystem")
   /**
     * ActorRef of the actor*/
   val myActor: ActorRef = system.actorOf(Props(new PlayerActor(this)))

   var myTurn: Boolean = _


  def getCardsFirstPlayer(cards: List[String]): Unit = {
    playGameController.getCardsFirstPlayer(cards.asJava)
  }

  def getBriscolaChosen(briscola: String): Unit = {
    playGameController.getBriscolaChosen(briscola)
  }

  def getCommand(command: String, player: Player): Unit = {
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

  def selectBriscola() = {
    playGameController.showBriscolaCommands()
  }


   /** Metodo per inviare al PlayerActor il comando cliccato dalla gui */
   def setCommandFromPlayer(command: String): Unit = {
    myActor ! ClickedCommand(command, myActor)
   }

   def selectBriscola(briscola: String): Unit = {
     // creo un seed e gli metto come campo briscola
      //myActor ! BriscolaChosen(briscola)
   }

   def setPlayedCard(cardIndex: Int): Unit ={
     myActor ! ClickedCard(cardIndex, myActor)
   }

   def isMyTurn(): Boolean = {
     return myTurn
   }

   def setMyTurn(turn: Boolean): Unit = {
     this.myTurn = turn
   }


}
