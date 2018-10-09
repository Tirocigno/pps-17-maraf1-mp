package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import akka.actor.ActorRef
import it.unibo.pps2017.core.player.Player
import it.unibo.pps2017.core.playerActor.PlayerActor._
import it.unibo.pps2017.core.deck.cards._
import it.unibo.pps2017.core.deck.cards.Seed.Seed

object PlayerActor {
  case class DistributedCard(cards: Set[Card], player: ActorRef)
  case class SelectBriscola(player: ActorRef) // la manda il controller per capire chi deve fare le bris
  case class BriscolaChosen(seed: Seed) // la manda il controller a tutti per dire la bris scelta
  case class NotifyBriscolaChosen(seed: Seed)
  case class Turn(player: Player, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  case class ClickedCard(index: Int, player: ActorRef)
  case class EndTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  case class PlayedCard(card: Card, player: Player)
  case class ClickedCommand(command: String, player: ActorRef)
  case class NotifyCommandChose(command: String, player: Player)
  case class ForcedCardPlayed(card: Card, player: Player)
}


  class PlayerActor(clientController: ClientController) extends Actor {

    /* Qui dovro' inserire il mio player */
    var player : Player = _

  def receive: PartialFunction[Any, Unit] = {

    case DistributedCard(cards, player) => {

      /* Se il messaggio che arriva rappresenta le mie carte, le invio al ClientController */
      if (this.player.eq(player)) {
        /**
          * Quando Ulio avra' fatto, chiamero':
          * clientController.getCardsFirstPlayer(cards)
          */
      }

    }


    case SelectBriscola(player) => {
      if (this.player.eq(player)) {
        clientController.selectBriscola()
      }
    }

    case BriscolaChosen(seed) => {
      /** inviare al GameActor la briscola scelta */
    }

    case NotifyBriscolaChosen(seed) => {
      /**  Quando Ulio avra' fatto chiamero':
            clientController.getBriscolaChosen(seed)
        */
    }

    case ClickedCard(index, player) => {
      /** inviare l'indice della carta scelta al GameActor */

    }

    case ClickedCommand(command, player) => {
      /** inviare comando e giocatore al GameActor */
    }

    case Turn(player, endPartialTurn, isFirstPlayer) => {
      /* se e' il mio turno, setto la variabile isMyTurn a true */
      if (this.player.eq(player)) {
        clientController.setMyTurn(true)
      } else {
        clientController.setMyTurn(false)
      }
      clientController.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
    }

    case EndTurn(firstTeamScore, secondTeamScore, endMatch) => {
      clientController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
    }

    case PlayedCard(card, player) => {
      // qui dovro' convertire da carta a path
      //clientController.showOtherPlayersPlayedCard(card, player)
    }

    case NotifyCommandChose(player, command) => {
      clientController.getCommand(player, command)
    }

    case ForcedCardPlayed(card, player) => {
      // qui dovro' convertire da carta a path
      //clientController.showOtherPlayersPlayedCard(card, player)
    }

  }

     def myTurn: Boolean = {
      return this.isMyTurn
    }


}
