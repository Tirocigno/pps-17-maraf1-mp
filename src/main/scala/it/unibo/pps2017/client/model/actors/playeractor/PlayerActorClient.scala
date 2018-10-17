
package it.unibo.pps2017.client.model.actors.playeractor

import akka.actor.{ActorRef, Stash}
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


class PlayerActorClient(override val controller: GameController, username: String) extends ClientGameActor with Stash {

  var actorPlayer: ClientGameActor = this
  var user: String = username
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _
  var cardArrived: Boolean = false

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      this.gameActor = sender()
      val finalList: ListBuffer[String] = this.orderPlayersList(playersList)
      controller.updateGUI(PlayersRef(finalList))

    case DistributedCard(cards, player) =>
      if (this.user.eq(player))
        controller.updateGUI(DistributedCard(cards, player))
        cardArrived = true
        unstashAll()

    case SelectBriscola(player) =>
      if (this.user.eq(player))
        if (!cardArrived) {
          stash()
        } else {
          controller.updateGUI(SelectBriscola(player))
          cardArrived = false
        }

    case BriscolaChosen(seed) =>
      gameActor ! BriscolaChosen(seed)

    case NotifyBriscolaChosen(seed) =>
      controller.updateGUI(NotifyBriscolaChosen(seed))
      gameActor ! BriscolaAck

    case ClickedCardActualPlayer(index) =>
      gameActor ! ClickedCard(index, user)

    case CardOk(correctClickedCard) =>
      controller.updateGUI(CardOk(correctClickedCard))

    case ClickedCommandActualPlayer(command) =>
      gameActor ! ClickedCommand(command, user)

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (this.user.eq(player)) {
        controller.setMyTurn(true)
      } else {
        controller.setMyTurn(false)
      }
      controller.updateGUI(Turn(player, endPartialTurn, isFirstPlayer))

    case PlayedCard(card, player) =>
      controller.updateGUI(PlayedCard(card, player))
      gameActor ! CardPlayedAck

    case NotifyCommandChosen(command, player) =>
      controller.updateGUI(NotifyCommandChosen(command, player))

    case ForcedCardPlayed(card, player) =>
      controller.updateGUI(ForcedCardPlayed(card, player))
      gameActor ! CardPlayedAck

    case SetTimer(timer) =>
      controller.updateGUI(SetTimer(timer))

    case PartialGameScore(winner1, winner2, score1, score2) =>
      controller.updateGUI(ComputePartialGameScore(user, winner1, winner2, score1, score2))

    case FinalGameScore(winner1, winner2, score1, score2) =>
      controller.updateGUI(ComputeFinalGameScore(user, winner1, winner2, score1, score2))

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
