
package it.unibo.pps2017.client.model.actors.playeractor

import akka.actor.{ActorRef, PoisonPill, Stash}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.client.model.actors.playeractor.PlayerActorClient._
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

object PlayerActorClient {
  final val START_SEARCH: Int = 0
  final val FOUNDED: Int = 1
  final val END_SEARCH: Int = 4
}

class PlayerActorClient(override val controller: GameController, playerid: String) extends ClientGameActor with Stash {

  var actorPlayer: ClientGameActor = this
  var user: String = playerid
  var orderedPlayersList = new ListBuffer[String]()
  var gameActor: ActorRef = _
  var cardArrived: Boolean = false
  var finalList: ListBuffer[String] = ListBuffer[String]()

  def receive: PartialFunction[Any, Unit] = {

    case PlayersRef(playersList) =>
      gameActor = sender
      communicatePlayersMatch(playersList)

    case DistributedCard(cards, player) => communicatePlayersCard(cards, player)

    case SelectBriscola(player) => informPlayerToChooseBriscola(player)

    case BriscolaChosen(seed) => sendBriscolaChosen(seed)

    case NotifyBriscolaChosen(seed) => notifyBriscolaChosen(seed)

    case ClickedCardActualPlayer(index) => sendIndexClickedCard(index)

    case CardOk(correctClickedCard, player) => cardOk(correctClickedCard, player)

    case ClickedCommandActualPlayer(command) => sendClickedCommand(command)

    case Turn(player, endPartialTurn, isFirstPlayer) => communicateTurn(player, endPartialTurn, isFirstPlayer)

    case PlayedCard(card, player) => communicatePlayedCard(card, player)

    case NotifyCommandChosen(command, player) => notifyCommandChosen(command, player)

    case GameScore(winner1, winner2, score1, score2, endMatch) => communicateGameScore(winner1, winner2, score1, score2, endMatch)

    case SetUsernamePlayer(playerUsername) => setUsername(playerUsername)

    case IdChannelPublishSubscribe(id) => registerToChannel(id)

    case NotifyClosedPlayGameView() => notifyClosedGame()
  }

  private def communicatePlayersMatch(playersList: ListBuffer[String]): Unit = {
    finalList = orderPlayersList(playersList)
    controller.updateGUI(PlayersRef(finalList))
    gameActor ! PlayersRefAck
  }

  private def communicatePlayersCard(cards: List[String], player: String): Unit = {
    if (user.equals(player)) {
      controller.updateGUI(DistributedCard(cards, player))
      cardArrived = true
      unstashAll
    }
  }

  private def informPlayerToChooseBriscola(player: String): Unit = {
    if (user.equals(player))
      if (!cardArrived)
        stash
      else
      controller.updateGUI(SelectBriscola(player))
      cardArrived = false
  }

  private def sendBriscolaChosen(seed: Seed): Unit =
    gameActor ! BriscolaChosen(seed)

  private def notifyBriscolaChosen(seed: Seed): Unit =
    controller.updateGUI(NotifyBriscolaChosen(seed))
    gameActor ! BriscolaAck

  private def sendIndexClickedCard(index: Int): Unit =
    gameActor ! ClickedCard(index, user)

  private def cardOk(correctClickedCard: Boolean, player: String): Unit =
    if (user.equals(player)) controller.updateGUI(CardOk(correctClickedCard, player))

  private def sendClickedCommand(command: String): Unit =
    gameActor ! ClickedCommand(command, user)

  private def communicateTurn(player: String, endPartialTurn: Boolean, isFirstPlayer: Boolean): Unit = {
    if (user.equals(player)) controller.setMyTurn(true)
    else  controller.setMyTurn(false)
    controller.updateGUI(Turn(player, endPartialTurn, isFirstPlayer))
  }

  private def communicatePlayedCard(card: String, player: String): Unit =
    controller.updateGUI(PlayedCard(card, player))
    gameActor ! CardPlayedAck

  private def notifyCommandChosen(command: String, player: String): Unit =
    controller.updateGUI(NotifyCommandChosen(command, player))

  private def communicateGameScore(winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean): Unit =
    controller.updateGUI(ComputeGameScore(user, winner1, winner2, score1, score2, endMatch))

  private def setUsername(playerUsername: String): Unit = user = playerUsername

  private def registerToChannel(id: String): Unit = {
    val mediator = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(id, self)
  }

  private def notifyClosedGame(): Unit =
    gameActor ! ClosedPlayGameView(user)
    self ! PoisonPill

  private def orderPlayersList(playersList: ListBuffer[String]): ListBuffer[String] = {
    val tempList = playersList ++ playersList
    var searchPlayer = START_SEARCH
    var orderedList = new ListBuffer[String]

    for (player <- tempList) player match {
      case actualPlayer if actualPlayer.equals(user) & searchPlayer == START_SEARCH
        => (orderedList += actualPlayer, searchPlayer += FOUNDED)
      case actualPlayer if !actualPlayer.equals(user) & !(searchPlayer == START_SEARCH) & searchPlayer < END_SEARCH
        => (orderedList += actualPlayer, searchPlayer += FOUNDED)
      case _ =>
    }
    orderedList
  }

  override def username: String =
    user
}
