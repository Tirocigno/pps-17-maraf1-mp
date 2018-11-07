package it.unibo.pps2017.client.controller.actors.playeractor

import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.view.game.GameGUIController

/**
  * MatchController is the mediator between the playerActor and the GUI.
  */
trait MatchController extends ActorController {
  /**
    * Set Gui controller inside the match controller.
    *
    * @param gui the gui controller to bind.
    */
  def setCurrentGui(gui: GameGUIController)
}
