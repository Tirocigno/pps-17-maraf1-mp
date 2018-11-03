
package it.unibo.pps2017.client.view

/**
  * Mock trait to be extended by all GUI's Controllers.
  */
trait GUIController {


  /**
    * Notify an error thrown to GUI.
    *
    * @param throwable the error to throw.
    */
  def notifyError(throwable: Throwable)
}
