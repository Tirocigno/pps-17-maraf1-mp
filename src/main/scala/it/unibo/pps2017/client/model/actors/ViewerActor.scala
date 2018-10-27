
package it.unibo.pps2017.client.model.actors

import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._

class ViewerActor(override val controller: GameController, username: String) extends ClientGameActor {

  var actorPlayer: ClientGameActor = this
  var user: String = username

  def receive: PartialFunction[Any, Unit] = {

    case RecapActualSituation(playersList, cards, seed, player) => {
      controller.updateGUI(PlayersRef(playersList))
      controller.updateGUI(DistributedCard(cards, username))
      controller.updateGUI(NotifyBriscolaChosen(seed))
      controller.updateGUI(Turn(player, true, false))
    }


  }


  override
  def getUsername: String = {
    user
  }

}
