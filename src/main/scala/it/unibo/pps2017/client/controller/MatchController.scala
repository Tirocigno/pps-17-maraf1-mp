
package it.unibo.pps2017.client.controller

import it.unibo.pps2017.client.view.game.GameGUIController

/**
  * MatchController is the mediator between the playerActor and the GUI.
  */
trait MatchController extends ActorController {

  override var currentGUI: GameGUIController = _

  override def setGUI[A <: GameGUIController](gui: A): Unit = this.currentGUI = gui
}
