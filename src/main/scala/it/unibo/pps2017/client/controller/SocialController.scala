
package it.unibo.pps2017.client.controller

import it.unibo.pps2017.commons.remote.social.SocialUtils.SocialMap


/**
  * This trait is a mediator between the actor that handle the social
  * function of the system and the GUI.
  *
  */
//TODO DEVELOP GUI INTERACTION.
trait SocialController extends ActorController {

  /**
    * Notify a API callback response to the GUI
    *
    * @param message the body of the response.
    */
  def notifyCallResultToGUI(message: Option[String]): Unit

  def setAndDisplayOnlinePlayerList(playerList: SocialMap): Unit
}
