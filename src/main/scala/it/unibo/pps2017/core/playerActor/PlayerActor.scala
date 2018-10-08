package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import it.unibo.pps2017.core.player._
import it.unibo.pps2017.core.player.Command.Command
import it.unibo.pps2017.core.playerActor.PlayerActor._
import it.unibo.pps2017.core.gui.PlayGameController
import collection.JavaConverters._


object PlayerActor {
  case class DistributedCardMsg (cards: List[String])
  case class SelectBriscolaMsg()
  case class GetBriscolaChosenMsg(briscola: String)
  case class TurnMsg(player: Player, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  case class ClickedCardMsg(index: Int)
  case class EndTurnMsg(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  case class PlayedCardMsg (path: String, player: Player)
  case class ClickedCommandMsg(command: String)
  case class NotifyCommandMsg (command: Command, player: Player)
}


abstract class PlayerActor extends Actor {

  val  playGameController: PlayGameController

  def receive: PartialFunction[Any, Unit] = {

    case DistributedCardMsg(cards) => {
      playGameController.getCardsFirstPlayer(cards.asJava)
    }

    case GetBriscolaChosenMsg(briscola) => {
      playGameController.getBriscolaChosen(briscola)
    }

    case SelectBriscolaMsg() => {


    }

    case TurnMsg(player, endPartialTurn, isFirstPlayer) => {

    }

    case EndTurnMsg(firstTeamScore, secondTeamScore, endMatch) => {

    }

    case PlayedCardMsg(path, player) => {
      // importare nel PlayGameController il Player di scala e fare le sostituzioni
      //playGameController.showOtherPlayersPlayedCard(player, path)
    }

    case NotifyCommandMsg(command, player) => {
      //playGameController.getCommand(player, command)
    }

  }


}
