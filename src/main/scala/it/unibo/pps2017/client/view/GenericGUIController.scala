package it.unibo.pps2017.client.view

import it.unibo.pps2017.commons.remote.rest.RestUtils.MatchRef

trait GenericGUIController extends GUIController {

  /**
    * Notify an error occurred inside the model to GUI.
    *
    * @param throwable the error occurred.
    */
  def notifyError(throwable: Throwable): Unit

  /**
    * Display a match list inside the GUI.
    *
    * @param matchesList the list which will be displayed.
    */
  def displayMatchesList(matchesList: List[MatchRef])
}