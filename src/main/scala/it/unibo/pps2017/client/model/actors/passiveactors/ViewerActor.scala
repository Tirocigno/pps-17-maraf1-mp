
package it.unibo.pps2017.client.model.actors.passiveactors

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.playeractor.ClientGameActor
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

class ViewerActor(override val controller: GameController, var player: String) extends ClientGameActor {

  import context._

  var actorPlayer: ClientGameActor = this
  var firstPlayer: String = player

  def receive: PartialFunction[Any, Unit] = {

    case IdChannelPublishSubscribe(id) =>
      registerToChannel(id)

    case RecapActualSituation(playersList, cards, seed, playerToView) =>
      communicateRecapActualSituation(playersList, cards, seed, playerToView)

      become({

        case DistributedCard(cardsOfPlayer, actualPlayer) =>
          communicatePlayersCard(cardsOfPlayer, actualPlayer)

        case NotifyBriscolaChosen(seedChosen) =>
          notifyBriscolaChosen(seedChosen)

        case Turn(actualPlayer, endPartialTurn, _, _) =>
          communicateTurn(actualPlayer, endPartialTurn)

        case PlayedCard(card, actualPlayer) =>
          communicatePlayedCard(card, actualPlayer)

        case NotifyCommandChosen(command, actualPlayer) =>
          notifyCommandChosen(command, actualPlayer)

        case GameScore(winner1, winner2, score1, score2, endMatch) =>
          communicateGameScore(winner1, winner2, score1, score2, endMatch)

      }, discardOld = true)
  }

  private def registerToChannel(id: String): Unit = {
    val mediator = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(id, self)
  }

  private def communicateRecapActualSituation(playersList: ListBuffer[String], cards: ListBuffer[String],
                                              seed: Seed, player: String): Unit = {
    controller.updateGUI(PlayersRef(playersList))
    firstPlayer = playersList.head
    controller.updateGUI(DistributedCard(cards.toList, firstPlayer))
    controller.updateGUI(NotifyBriscolaChosen(seed = seed))
    controller.updateGUI(Turn(player, endPartialTurn = true, isFirstPlayer = false, isReplay = false))
  }

  private def communicatePlayersCard(cardsOfPlayer: List[String], actualPlayer: String): Unit =
    if (actualPlayer.equals(firstPlayer))
      controller.updateGUI(DistributedCard(cardsOfPlayer, actualPlayer))

  private def notifyBriscolaChosen(seedChosen: Seed): Unit =
    controller.updateGUI(NotifyBriscolaChosen(seed = seedChosen))

  private def communicateTurn(actualPlayer: String, endPartialTurn: Boolean): Unit =
    controller.updateGUI(Turn(actualPlayer, endPartialTurn, isFirstPlayer = false, isReplay = false))

  private def communicatePlayedCard(card: String, actualPlayer: String): Unit =
    controller.updateGUI(PlayedCard(card, actualPlayer))

  private def notifyCommandChosen(command: String, actualPlayer: String): Unit =
    controller.updateGUI(NotifyCommandChosen(command, actualPlayer))

  private def communicateGameScore(winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean): Unit =
    controller.updateGUI(ComputeGameScore(firstPlayer, winner1, winner2, score1, score2, endMatch))

  override
  def username: String = firstPlayer
}
