
package it.unibo.pps2017.client.view.login

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

}
