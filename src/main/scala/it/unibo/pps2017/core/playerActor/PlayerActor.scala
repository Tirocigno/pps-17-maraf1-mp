package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.deck.cards._
import it.unibo.pps2017.core.playerActor.PlayerActor._

object PlayerActor {
  case class Start(playersList: List[PlayerActor])
  case class DistributedCard(cards: Set[Card], player: PlayerActor)
  case class SelectBriscola(player: PlayerActor) // la manda il controller per capire chi deve fare le bris
  case class BriscolaChosen(seed: Seed) // la manda il controller a tutti per dire la bris scelta
  case class NotifyBriscolaChosen(seed: Seed)
  case class Turn(player: PlayerActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  case class ClickedCard(index: Int, player: PlayerActor)
  case class EndTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  case class PlayedCard(card: Card, player: PlayerActor)
  case class ClickedCommand(command: String, player: PlayerActor)
  case class NotifyCommandChose(command: String, player: PlayerActor)
  case class ForcedCardPlayed(card: Card, player: PlayerActor)
}


  class PlayerActor(clientController: ClientController, username: String) extends Actor {

    var player : PlayerActor = this // mio player
    var user : String = username // username del player
    var orderedPlayersList : List[String] = _

  def receive: PartialFunction[Any, Unit] = {



    case Start(playersList) => {
      // qui devo ordinare la mia lista mettendo me in testa

      clientController.sendPlayersList(orderedPlayersList)
    }

    case DistributedCard(cards, player) => {

      /* Se il messaggio che arriva e' destinato a me, invio le carte al ClientController */
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
      /** inviare al GameActor la briscola scelta
        * gameActor ! BriscolaChosen(seed)
        * * */
    }

    case NotifyBriscolaChosen(seed) => {
      /**  Quando Ulio avra' fatto chiamero':
            clientController.getBriscolaChosen(seed.getType)
        */
    }

    case ClickedCard(index, player) => {
      /** inviare l'indice della carta scelta al GameActor
        * gameActor ! ClickedCard(index, this)
        * */

    }

    case ClickedCommand(command, player) => {
      /** inviare comando e giocatore al GameActor
        * gameActor ! ClickedCommand(command, this)
        * */
    }

    case Turn(player, endPartialTurn, isFirstPlayer) => {
      /* se e' il mio turno, setto la variabile isMyTurn a true */
      if (this.player.eq(player)) {
        clientController.setMyTurn(true)
      } else {
        clientController.setMyTurn(false)
      }
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)
    }

    case EndTurn(firstTeamScore, secondTeamScore, endMatch) => {
      clientController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)
    }

    case PlayedCard(card, player) => {
      // qui dovro' convertire da carta a path
      //clientController.showOtherPlayersPlayedCard(card, player)
    }

    case NotifyCommandChose(command, player) => {
      clientController.getCommand(command, player.getUsername)
    }

    case ForcedCardPlayed(card, player) => {
      // qui dovro' convertire da carta a path
      //clientController.showOtherPlayersPlayedCard(card, player.getUsername)
    }

  }

    def getUsername: String = {
      return user
    }


}
