
package it.unibo.pps2017.client.view.login

import it.unibo.pps2017.client.view.GUIController

/**
  * Trait for a login scene controller.
  */
trait LoginController extends GUIController {

  /**
    * Set the response inside the controller.
    *
    * @param message
    */
  def handleResponse(message: String)
}
