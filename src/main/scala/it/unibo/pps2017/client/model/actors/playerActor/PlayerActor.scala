package it.unibo.pps2017.client.model.actors.playerActor

import akka.actor.ActorRef
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.model.actors.GameActor
import it.unibo.pps2017.client.model.actors.playerActor.PlayerActor._
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

object PlayerActor {

  case class PlayersRef(playersList: ListBuffer[GameActor])

  case class DistributedCard(cards: List[String], player: GameActor)

  case class SelectBriscola(player: GameActor)

  case class BriscolaChosen(seed: Seed)

  case class NotifyBriscolaChosen(seed: Seed)

  case class Turn(player: GameActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)

  case class ClickedCard(index: Int, player: GameActor)

  case class PlayedCard(card: String, player: GameActor)

  case class ClickedCommand(command: String, player: GameActor)

  case class NotifyCommandChose(command: String, player: GameActor)

  case class ForcedCardPlayed(card: String, player: GameActor)

  case class CardOk(correctClickedCard: Boolean)

  case class SetTimer(timer: Int)

  case class PartialGameScore(winner1: GameActor, winner2: GameActor, score1: Int, score2: Int)

  case class FinalGameScore(winner1: GameActor, winner2: GameActor, score1: Int, score2: Int)

  case class IdChannelPublishSubscribe(id: String)

  case class BriscolaAck()

  case class CardPlayedAck()

  final val START_SEARCH: Int = 0
  final val FOUNDED: Int = 1
  final val END_SEARCH: Int = 4
}


class PlayerActor(override val controller: GameController, username: String) extends GameActor {

  var actorPlayer: GameActor = this
  var user: String = username
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      this.gameActor = sender()
      val finalList: ListBuffer[String] = this.orderPlayersList(playersList)
      controller.sendPlayersList(finalList.toList)

    case DistributedCard(cards, player) =>
      if (this.actorPlayer.eq(player))
        controller.getCardsFirstPlayer(cards)

    case SelectBriscola(player) =>
      if (this.actorPlayer.eq(player))
        controller.selectBriscola()

    case BriscolaChosen(seed) =>
      gameActor ! BriscolaChosen(seed)

    case NotifyBriscolaChosen(seed) =>
      controller.getBriscolaChosen(briscola = seed.asString)
      gameActor ! BriscolaAck

    case ClickedCard(index, player) =>
      gameActor ! ClickedCard(index, this.actorPlayer)

    case CardOk(correctClickedCard) =>
      controller.setCardOK(correctClickedCard)

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
        controller.setMyTurn(true)
      } else {
        controller.setMyTurn(false)
      }
      controller.setCurrentPlayer(player.getUsername, endPartialTurn, isFirstPlayer)

    case PlayedCard(card, player) =>
      controller.showOtherPlayersPlayedCard(card, player.getUsername)
      gameActor ! CardPlayedAck

    case NotifyCommandChose(command, player) =>
      controller.getCommand(player.getUsername, command)

    case ForcedCardPlayed(card, player) =>
      controller.showOtherPlayersPlayedCard(card, player = player.getUsername)
      gameActor ! CardPlayedAck

    case SetTimer(timer) =>
      controller.setTimer(timer)

    case PartialGameScore(winner1, winner2, score1, score2) =>
      if (this.actorPlayer.eq(winner1) | this.actorPlayer.eq(winner2)) {
        if (score1 > score2) controller.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
        else controller.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
      } else {
        if (score1 > score2) controller.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
        else controller.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
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
          controller.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          controller.setWinner(true)
        } else {
          controller.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          controller.setWinner(true)
        }
      } else {
        if (score1 > score2) {
          controller.cleanFieldEndTotalTurn(score2, score1, endMatch = true)
          controller.setWinner(false)
        }
        else {
          controller.cleanFieldEndTotalTurn(score1, score2, endMatch = true)
          controller.setWinner(false)
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

  override
   def getUsername: String = {
    user
  }

  private def orderPlayersList(playersList: ListBuffer[GameActor]): ListBuffer[String] = {
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
