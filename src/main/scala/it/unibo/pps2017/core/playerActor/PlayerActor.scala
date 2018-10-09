package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import akka.actor.ActorRef
import it.unibo.pps2017.core.player.Player
import it.unibo.pps2017.core.player.Command.Command
import it.unibo.pps2017.core.playerActor.PlayerActor._
import it.unibo.pps2017.core.deck.cards._

object PlayerActor {
  case class DistributedCard(cards: Set[Card], player: ActorRef)
  case class SelectBriscola(briscola: String)
  case class GetBriscolaChosen(briscola: String)
  case class Turn(player: Player, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  case class ClickedCard(index: Int)
  case class EndTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  case class PlayedCard(path: String, player: Player)
  case class ClickedCommand(command: String, player: Player)
  case class NotifyCommand(command: Command, player: Player)
}


  class PlayerActor(clientController: ClientController) extends Actor {

    /* Qui dovro' inserire il mio player */
    var player : Player = _


  def receive: PartialFunction[Any, Unit] = {

    case DistributedCard(cards, player) => {

      /* Se il messaggio che arriva rappresenta le mie carte, le invio al ClientController */
      if (this.player.eq(player)) {
        val cards1 = List("")
        clientController.getCardsFirstPlayer(cards1)
        /**
          * Quando Ulio avra' fatto, chiamero':
          * clientController.getCardsFirstPlayer(cards)
          */
      }

    }

    case GetBriscolaChosen(briscola) => {
      clientController.getBriscolaChosen(briscola)
    }

    case SelectBriscola(briscola) => {
      /** inviare briscola scelta al GameActor */
    }

    case ClickedCard(index) => {
      /** inviare l'indice della carta scelta al GameActor */

    }

    case ClickedCommand(command, player) => {
      /** inviare comando e giocatore al GameActor */
    }

    case Turn(player, endPartialTurn, isFirstPlayer) => {
      clientController.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
    }

    case EndTurn(firstTeamScore, secondTeamScore, endMatch) => {
      clientController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
    }

    case PlayedCard(path, player) => {
      clientController.showOtherPlayersPlayedCard(path, player)
    }

    case NotifyCommand(player, command) => {
      clientController.getCommand(player, command)
    }

  }


}
