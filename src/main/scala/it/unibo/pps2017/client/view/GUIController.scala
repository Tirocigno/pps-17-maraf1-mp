package it.unibo.pps2017.client.view

import it.unibo.pps2017.client.controller.Controller


/**
  * Mock trait to be extended by all GUI's Controllers
  */
trait GUIController {
  def setController(controller: Controller)
}
