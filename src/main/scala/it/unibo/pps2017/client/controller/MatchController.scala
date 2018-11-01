
package it.unibo.pps2017.client.controller

import it.unibo.pps2017.client.view.game.GameGUIController


/**
  * MatchController is the mediator between the playerActor and the GUI.
  */
trait MatchController extends ActorController {
  def setCurrentGui(gui: GameGUIController)
}
