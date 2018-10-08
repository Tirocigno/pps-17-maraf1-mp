package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import it.unibo.pps2017.core.player.Player
import it.unibo.pps2017.core.player.Command.Command
import it.unibo.pps2017.core.playerActor.PlayerActor._

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

  val  clientController: ClientController

  def receive: PartialFunction[Any, Unit] = {

    case DistributedCardMsg(cards) => {
      clientController.getCardsFirstPlayer(cards)
    }

    case GetBriscolaChosenMsg(briscola) => {
      clientController.getBriscolaChosen(briscola)
    }

    case SelectBriscolaMsg() => {

    }

    case ClickedCardMsg(index) => {

    }

    case ClickedCommandMsg(command) => {

    }

    case TurnMsg(player, endPartialTurn, isFirstPlayer) => {
      clientController.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
    }

    case EndTurnMsg(firstTeamScore, secondTeamScore, endMatch) => {
      clientController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
    }

    case PlayedCardMsg(path, player) => {
      clientController.showOtherPlayersPlayedCard(path, player)
    }

    case NotifyCommandMsg(player, command) => {
      clientController.getCommand(player, command)
    }

  }


}
