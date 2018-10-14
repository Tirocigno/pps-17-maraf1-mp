package it.unibo.pps2017.core.playerActor

import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
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

  case class IdChannelPublishSubscribe(id: String)

  case class BriscolaAck()

  case class CardPlayedAck()

  final val START_SEARCH: Int = 0
  final val FOUNDED: Int = 1
  final val END_SEARCH: Int = 4
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
      clientController.getBriscolaChosen(briscola = seed.asString)
      gameActor ! BriscolaAck

    case ClickedCard(index, player) =>
      gameActor ! ClickedCard(index, this.actorPlayer)

    case CardOk(correctClickedCard) =>
      clientController.setCardOK(correctClickedCard)

    case ClickedCommand(command, player) =>
      gameActor ! ClickedCommand(command, this.actorPlayer)


    /*
  case Turn(player, endPartialTurn, isFirstPlayer) => player match {
    case player if player == actorPlayer  => clientController.setMyTurn(true)
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)
    case _ => clientController.setMyTurn(false)
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)
  } */

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (this.actorPlayer.eq(player)) {
        clientController.setMyTurn(true)
      } else {
        clientController.setMyTurn(false)
      }
      clientController.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)

    case PlayedCard(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player.getUsername)
      gameActor ! CardPlayedAck

    case NotifyCommandChose(command, player) =>
      clientController.getCommand(player.getUsername, command)

    case ForcedCardPlayed(card, player) =>
      clientController.showOtherPlayersPlayedCard(card, player = player.getUsername)
      gameActor ! CardPlayedAck

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

    /*
  case PartialGameScore(winner1, winner2, score1, score2) => (winner1, winner2) match {
    case (`winner1`, `winner2`) if winner1.eq(actorPlayer) | winner2.eq(actorPlayer) => (score1, score2) match {
      case `score1` > `score2` => clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
      case _ => clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
    }
    case _ => (score1, score2) match {
      case `score1` > `score2` => clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
      case _ => clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
    }
  } */

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

/*
    case FinalGameScore(winner1, winner2, score1, score2) => (winner1, winner2) match {
      case (`winner1`, `winner2`) if winner1.eq(actorPlayer) | winner2.eq(actorPlayer) => (score1, score2) match {
        case `score1` > `score2` =>
          clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          clientController.setWinner(true)
        case _ =>
          clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          clientController.setWinner(true)
      }
      case _ => (score1, score2) match {
        case `score1` > `score2` =>
          clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          clientController.setWinner(false)
        case _ =>
          clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          clientController.setWinner(false)
      }
    }
      */


    case IdChannelPublishSubscribe(id) =>
      val mediator = DistributedPubSub(context.system).mediator
      mediator ! Subscribe(id, self)
  }

  private def getUsername: String = {
    user
  }

  private def orderPlayersList(playersList: ListBuffer[PlayerActor]): ListBuffer[String] = {
    val tempList = playersList ++ playersList
    var searchPlayer = START_SEARCH
    var orderedList = new ListBuffer[String]

    for (player <- tempList) {
      player match {
        case `player` if player.eq(actorPlayer) & searchPlayer.eq(START_SEARCH)
        => (orderedList += player.getUsername, searchPlayer += FOUNDED)
        case `player` if player.eq(actorPlayer) & !searchPlayer.eq(START_SEARCH) & searchPlayer < END_SEARCH
        => (orderedList += player.getUsername, searchPlayer += FOUNDED)
        case _ => tempList.clear
      }
    }
    orderedList
  }
}
