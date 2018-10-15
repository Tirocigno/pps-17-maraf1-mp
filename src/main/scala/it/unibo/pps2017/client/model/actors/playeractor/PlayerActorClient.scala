
package it.unibo.pps2017.client.model.actors.playeractor

import akka.actor.ActorRef
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ClientGameActor
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient._

import scala.collection.mutable.ListBuffer

object PlayerActorClient {

  final val START_SEARCH: Int = 0
  final val FOUNDED: Int = 1
  final val END_SEARCH: Int = 4
}


class PlayerActorClient(override val controller: GameController, username: String) extends ClientGameActor {

  var actorPlayer: ClientGameActor = this
  var user: String = username
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      this.gameActor = sender()
      val finalList: ListBuffer[String] = this.orderPlayersList(playersList)
      controller.sendPlayersList(finalList.toList)

    case DistributedCard(cards, player) =>
      if (this.user.eq(player))
        controller.sendCardsFirstPlayer(cards)

    case SelectBriscola(player) =>
      if (this.user.eq(player))
        controller.selectBriscola()

    case BriscolaChosen(seed) =>
      gameActor ! BriscolaChosen(seed)

    case NotifyBriscolaChosen(seed) =>
      controller.sendBriscolaChosen(briscola = seed.asString)
      gameActor ! BriscolaAck

    case ClickedCard(index, player) =>
      gameActor ! ClickedCard(index, player)

    case CardOk(correctClickedCard) =>
      controller.setCardOK(correctClickedCard)

    case ClickedCommand(command, player) =>
      gameActor ! ClickedCommand(command, player)


    /*
  case Turn(player, endPartialTurn, isFirstPlayer) => player match {
    case player if player.eq(user)  => clientController.setMyTurn(true)
      clientController.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
    case _ => clientController.setMyTurn(false)
      clientController.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)
  } */

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (this.user.eq(player)) {
        controller.setMyTurn(true)
      } else {
        controller.setMyTurn(false)
      }
      controller.setCurrentPlayer(player, endPartialTurn, isFirstPlayer)

    case PlayedCard(card, player) =>
      controller.showOtherPlayersPlayedCard(card, player)
      gameActor ! CardPlayedAck

    case NotifyCommandChose(command, player) =>
      controller.sendCommand(player, command)

    case ForcedCardPlayed(card, player) =>
      controller.showOtherPlayersPlayedCard(card, player = player)
      gameActor ! CardPlayedAck

    case SetTimer(timer) =>
      controller.setTimer(timer)

    case PartialGameScore(winner1, winner2, score1, score2) =>
      if (this.user.eq(winner1) | this.user.eq(winner2)) {
        if (score1 > score2) controller.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
        else controller.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
      } else {
        if (score1 > score2) controller.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
        else controller.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
      }

    /*
  case PartialGameScore(winner1, winner2, score1, score2) => (winner1, winner2) match {
    case (`winner1`, `winner2`) if winner1.eq(user) | winner2.eq(user) => (score1, score2) match {
      case `score1` > `score2` => clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
      case _ => clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
    }
    case _ => (score1, score2) match {
      case `score1` > `score2` => clientController.cleanFieldEndTotalTurn(score2, score1, endMatch = false)
      case _ => clientController.cleanFieldEndTotalTurn(score1, score2, endMatch = false)
    }
  } */

    case FinalGameScore(winner1, winner2, score1, score2) =>
      if (this.user.eq(winner1) | this.user.eq(winner2)) {
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
      case (`winner1`, `winner2`) if winner1.eq(user) | winner2.eq(user) => (score1, score2) match {
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

  private def orderPlayersList(playersList: ListBuffer[String]): ListBuffer[String] = {
    val tempList = playersList ++ playersList
    var searchPlayer = START_SEARCH
    var orderedList = new ListBuffer[String]

    for (player <- tempList) {
      player match {
        case `player` if player.eq(user) & searchPlayer == START_SEARCH
        => (orderedList += player, searchPlayer += FOUNDED)
        case `player` if player.eq(user) & !(searchPlayer == START_SEARCH) & searchPlayer < END_SEARCH
        => (orderedList += player, searchPlayer += FOUNDED)
        case _ => tempList.clear
      }
    }
    orderedList
  }
}
