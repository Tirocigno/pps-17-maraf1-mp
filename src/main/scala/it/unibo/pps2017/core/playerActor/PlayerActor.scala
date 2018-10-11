package it.unibo.pps2017.core.playerActor

import akka.actor.{Actor, ActorRef}
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.playerActor.PlayerActor._

import scala.collection.mutable.ListBuffer

object PlayerActor {

  case class PlayersRef(playersList: ListBuffer[PlayerActor])

  case class DistributedCard(cards: List[String], player: PlayerActor)

  case class SelectBriscola(player: PlayerActor)

  case class BriscolaChosen(seed: Seed)

  case class NotifyBriscolaChosen(seed: Seed)

  case class Turn(player: PlayerActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)

  case class ClickedCard(index: Int, player: PlayerActor)

  case class PlayedCard(card: String, player: PlayerActor)

  case class ClickedCommand(command: String, player: PlayerActor)

  case class NotifyCommandChose(command: String, player: PlayerActor)

  case class ForcedCardPlayed(card: String, player: PlayerActor)

  case class CardOk(correctClickedCard: Boolean)

  case class SetTimer(timer: Int)

  case class PartialGameScore(winner1: PlayerActor, winner2: PlayerActor, score1: Int, score2: Int)

  case class FinalGameScore(winner1: PlayerActor, winner2: PlayerActor, score1: Int, score2: Int)

  final val START_PLAYER_SEARCH: Int = 0
  final val ADD_PLAYER_FOUNDED: Int = 1
  final val END_PLAYER_SEARCH: Int = 4
}


class PlayerActor(clientController: ClientController, username: String) extends Actor {

  var actorPlayer: PlayerActor = this
  var user: String = username
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      this.gameActor = sender()
      val finalList: ListBuffer[String] = this.orderPlayersList(playersList)
      clientController.sendPlayersList(finalList.toList)

    case DistributedCard(cards, player) =>
      if (this.actorPlayer.eq(player))
        clientController.getCardsFirstPlayer(cards)

    case SelectBriscola(player) =>
      if (this.actorPlayer.eq(player))
        clientController.selectBriscola()

    case BriscolaChosen(seed) =>
      gameActor ! BriscolaChosen(seed)

    case NotifyBriscolaChosen(seed) =>
      clientController.getBriscolaChosen(briscola = seed.getSeed())

    case ClickedCard(index, player) =>
      gameActor ! ClickedCard(index, this.actorPlayer)

    case CardOk(correctClickedCard) =>
      clientController.setCardOK(correctClickedCard)

    case ClickedCommand(command, player) =>
      gameActor ! ClickedCommand(command, this.actorPlayer)

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (this.actorPlayer.eq(player)) {
        clientController.setMyTurn(true)
      } else {
        clientController.setMyTurn(false)
      }
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)

    case PlayedCard(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player.getUsername)

    case NotifyCommandChose(command, player) =>
      clientController.getCommand(player.getUsername, command)

    case ForcedCardPlayed(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player = player.getUsername)

    case SetTimer(timer) =>
      clientController.setTimer(timer)

    case PartialGameScore(winner1, winner2, score1, score2) =>
      if (this.actorPlayer.eq(winner1) | this.actorPlayer.eq(winner2)) {
        if (score1 > score2) clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
        else clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
      } else {
        if (score1 > score2) clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
        else clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
      }

    case FinalGameScore(winner1, winner2, score1, score2) =>
      if (this.actorPlayer.eq(winner1) | this.actorPlayer.eq(winner2)) {
        if (score1 > score2) {
          clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          clientController.setWinner(true)
        } else {
          clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          clientController.setWinner(true)
        }
      } else {
        if (score1 > score2) {
          clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          clientController.setWinner(false)
        }
        else {
          clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          clientController.setWinner(false)
        }
      }
  }

  private def getUsername: String = {
    user
  }

  private def orderPlayersList(playersList: ListBuffer[PlayerActor]): ListBuffer[String] = {

    val tempList = playersList ++ playersList
    var orderedList = new ListBuffer[String]

    var searchPlayer = START_PLAYER_SEARCH
    for (player <- tempList) {
      if (this.actorPlayer.eq(player) & searchPlayer.equals(START_PLAYER_SEARCH)) {
        orderedList += player.getUsername
        System.out.println(orderedList)
        searchPlayer += ADD_PLAYER_FOUNDED
      }
      if (searchPlayer != START_PLAYER_SEARCH & searchPlayer < END_PLAYER_SEARCH & !this.actorPlayer.eq(player)) {
        orderedList += player.getUsername
        searchPlayer += ADD_PLAYER_FOUNDED
      }
    }
    orderedList
  }
}
