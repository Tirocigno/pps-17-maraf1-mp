
package it.unibo.pps2017.client.view

import it.unibo.pps2017.client.controller.Controller
import it.unibo.pps2017.client.controller.clientcontroller.ClientController

/**
  * Mock trait to be extended by all GUI's Controllers.
  */
trait GUIController {


  /**
    * Set a controller inside the GUI.
    *
    * @param controller the controller to set inside.
    */
  def setController(controller: Controller)

  /**
    * Notify an error thrown to GUI.
    *
    * @param throwable the error to throw.
    */
  def notifyError(throwable: Throwable)
}
