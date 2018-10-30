
package it.unibo.pps2017.client.model.actors

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._

class ViewerActor(override val controller: GameController, username: String) extends ClientGameActor {

  import context._

  var actorPlayer: ClientGameActor = this
  var user: String = username
  var firstPlayer: String = user

  def receive: PartialFunction[Any, Unit] = {

    case IdChannelPublishSubscribe(id) =>
      val mediator = DistributedPubSub(context.system).mediator
      mediator ! Subscribe(id, self)

    case RecapActualSituation(playersList, cards, seed, player) =>
      controller.updateGUI(PlayersRef(playersList))
      firstPlayer = playersList.head
      controller.updateGUI(DistributedCard(cards, firstPlayer))
      controller.updateGUI(NotifyBriscolaChosen(seed = seed))
      controller.updateGUI(Turn(player, endPartialTurn = true, isFirstPlayer = false))

      become({

        case DistributedCard(cardsOfPlayer, actualPlayer) =>
          if (actualPlayer.equals(firstPlayer))
            controller.updateGUI(DistributedCard(cardsOfPlayer, actualPlayer))

        case NotifyBriscolaChosen(seedChosen) =>
          controller.updateGUI(NotifyBriscolaChosen(seed = seedChosen))

        case Turn(actualPlayer, endPartialTurn, _) =>
          controller.updateGUI(Turn(actualPlayer, endPartialTurn, isFirstPlayer = false))

        case PlayedCard(card, actualPlayer) =>
          controller.updateGUI(PlayedCard(card, actualPlayer))

        case NotifyCommandChosen(command, actualPlayer) =>
          controller.updateGUI(NotifyCommandChosen(command, actualPlayer))

        case PartialGameScore(winner1, winner2, score1, score2) =>
          controller.updateGUI(ComputePartialGameScore(firstPlayer, winner1, winner2, score1, score2))

        case FinalGameScore(winner1, winner2, score1, score2) =>
          controller.updateGUI(ComputeFinalGameScore(firstPlayer, winner1, winner2, score1, score2))

      }, discardOld = true)
  }


  override
  def getUsername: String = {
    user
  }

}
