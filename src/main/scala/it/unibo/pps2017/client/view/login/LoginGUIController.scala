
package it.unibo.pps2017.client.view.login

import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.view.GUIController

/**
  * Trait for a login scene controller.
  */
trait LoginGUIController extends GUIController {

  /**
    * Set the response inside the controller.
    *
    * @param message the message to display.
    */
  def handleResponse(message: String)

  /**
    * Set current GUI inside logincontroller.
    *
    * @param clientController clientController to register.
    */
  def setCurrentGUI(clientController: ClientController): Unit
}
