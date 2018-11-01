
package it.unibo.pps2017.client.model.actors.playeractor

import akka.actor.{ActorRef, PoisonPill, Stash}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ClientGameActor
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient._

import scala.collection.mutable.ListBuffer

object PlayerActorClient {
  val START_SEARCH: Int = 0
  val FOUNDED: Int = 1
  val END_SEARCH: Int = 4
}

class PlayerActorClient(override val controller: GameController, username: String) extends ClientGameActor with Stash {

  var actorPlayer: ClientGameActor = this
  var user: String = username
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _
  var cardArrived: Boolean = false

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      gameActor = sender()
      var finalList: ListBuffer[String] = ListBuffer[String]()
      finalList = orderPlayersList(playersList)
      controller.updateGUI(PlayersRef(finalList))
      gameActor ! PlayersRefAck

    case DistributedCard(cards, player) =>
      if (user.equals(player)) {
        controller.updateGUI(DistributedCard(cards, player))
        cardArrived = true
        unstashAll()
      }

    case SelectBriscola(player) =>
      if (user.equals(player))
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

    case CardOk(correctClickedCard, player) =>
      if (user.equals(player))
        controller.updateGUI(CardOk(correctClickedCard, player))

    case ClickedCommandActualPlayer(command) =>
      gameActor ! ClickedCommand(command, user)

    case Turn(player, endPartialTurn, isFirstPlayer) =>
      if (user.equals(player)) {
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

    case PartialGameScore(winner1, winner2, score1, score2) =>
      controller.updateGUI(ComputePartialGameScore(user, winner1, winner2, score1, score2))

    case FinalGameScore(winner1, winner2, score1, score2) =>
      controller.updateGUI(ComputeFinalGameScore(user, winner1, winner2, score1, score2))

    case SetUsernamePlayer(playerUsername) =>
      user = playerUsername

    case IdChannelPublishSubscribe(id) =>
      val mediator = DistributedPubSub(context.system).mediator
      mediator ! Subscribe(id, self)

    case ClosedPlayGameView(_) =>
      gameActor ! ClosedPlayGameView(user)
      self ! PoisonPill
  }

  private def orderPlayersList(playersList: ListBuffer[String]): ListBuffer[String] = {
    val tempList = playersList ++ playersList
    var searchPlayer = START_SEARCH
    var orderedList = new ListBuffer[String]

    for (player <- tempList) {
      player match {
        case actualPlayer if actualPlayer.equals(user) & searchPlayer == START_SEARCH
        => (orderedList += actualPlayer, searchPlayer += FOUNDED)
        case actualPlayer if !actualPlayer.equals(user) & !(searchPlayer == START_SEARCH) & searchPlayer < END_SEARCH
        => (orderedList += actualPlayer, searchPlayer += FOUNDED)
        case _ => tempList.clear
      }
    }
    orderedList
  }

  override
  def getUsername: String = {
    user
  }
}
