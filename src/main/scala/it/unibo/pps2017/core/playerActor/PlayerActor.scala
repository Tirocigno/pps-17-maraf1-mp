package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import it.unibo.pps2017.core.player.Player
import it.unibo.pps2017.core.player.Command.Command
import it.unibo.pps2017.core.playerActor.PlayerActor._

object PlayerActor {
  case class DistributedCardMsg (cards: List[String])
  case class SelectBriscolaMsg(briscola: String)
  case class GetBriscolaChosenMsg(briscola: String)
  case class TurnMsg(player: Player, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  case class ClickedCardMsg(index: Int)
  case class EndTurnMsg(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  case class PlayedCardMsg (path: String, player: Player)
  case class ClickedCommandMsg(command: String, player: Player)
  case class NotifyCommandMsg (command: Command, player: Player)
}


  class PlayerActor(clientController: ClientController) extends Actor {

  def receive: PartialFunction[Any, Unit] = {

    case DistributedCardMsg(cards) => {
      clientController.getCardsFirstPlayer(cards)
    }

    case GetBriscolaChosenMsg(briscola) => {
      clientController.getBriscolaChosen(briscola)
    }

    case SelectBriscolaMsg(briscola) => {
      /** inviare briscola scelta al GameActor */
    }

    case ClickedCardMsg(index) => {
      /** inviare l'indice della carta scelta al GameActor */

    }

    case ClickedCommandMsg(command, player) => {
      /** inviare comando e giocatore al GameActor */
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
