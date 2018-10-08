package it.unibo.pps2017.core.playerActor

import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.core.player._
import it.unibo.pps2017.core.player.Command.Command

import scala.collection.JavaConverters._


abstract class ClientController {

  val playGameController: PlayGameController
  val playerActor: PlayerActor

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



}
