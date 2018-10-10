package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.playerActor.PlayerActor._

import scala.collection.mutable.ListBuffer

object PlayerActor {

  case class PlayersRef(playersList: Set[PlayerActor])

  case class DistributedCard(cards: List[String], player: PlayerActor)

  case class SelectBriscola(player: PlayerActor) // la manda il controller per capire chi deve fare le bris

  case class BriscolaChosen(seed: Seed) // la mando io al GameActor per la bris che ho scelto

  case class NotifyBriscolaChosen(seed: Seed)

  case class Turn(player: PlayerActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)

  case class ClickedCard(index: Int, player: PlayerActor)

  case class EndTurn(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)

  case class PlayedCard(card: String, player: PlayerActor)

  case class ClickedCommand(command: String, player: PlayerActor)

  case class NotifyCommandChose(command: String, player: PlayerActor)

  case class ForcedCardPlayed(card: String, player: PlayerActor)

  case class CardOk(correctClickedCard: Boolean)

  case class SetTimer(timer: Int)

}


class PlayerActor(clientController: ClientController, username: String) extends Actor {

  var player: PlayerActor = this // mio player
  var user: String = username // username del player
  var orderedPlayersList = new ListBuffer[String]()

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      // qui devo ordinare la mia lista mettendo me in testa


      for (player <- playersList) if (this.player.eq(player)) orderedPlayersList += player.getUsername


      clientController.sendPlayersList(orderedPlayersList.toList)

    case DistributedCard(cards, player) =>
      if (this.player.eq(player))
        clientController.getCardsFirstPlayer(cards)

    case SelectBriscola(player) =>
      if (this.player.eq(player))
        clientController.selectBriscola()

    case BriscolaChosen(seed) =>
    /** inviare al GameActor la briscola scelta
      * gameActor ! BriscolaChosen(seed)
      * * */

    case NotifyBriscolaChosen(seed) =>
      clientController.getBriscolaChosen(briscola = seed.getSeed())

    case ClickedCard(index, player) =>
    /** inviare l'indice della carta scelta al GameActor
      * gameActor ! ClickedCard(index, this.player)
      * */

    case CardOk(correctClickedCard) =>
      clientController.setCardOK(correctClickedCard)

    case ClickedCommand(command, player) =>
    /** inviare comando e giocatore al GameActor
      * gameActor ! ClickedCommand(command, this.player)
      * */

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (this.player.eq(player)) {
        clientController.setMyTurn(true)
      } else {
        clientController.setMyTurn(false)
      }
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)

    case EndTurn(firstTeamScore, secondTeamScore, endMatch) =>
      clientController.cleanFieldEndTotalTurn(firstTeamScore, secondTeamScore, endMatch)

    case PlayedCard(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player.getUsername)

    case NotifyCommandChose(command, player) =>
      clientController.getCommand(command, player.getUsername)

    case ForcedCardPlayed(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player.getUsername)

    case SetTimer(timer) =>
      clientController.setTimer(timer)
  }


  private def getUsername: String = {
    user
  }


}
