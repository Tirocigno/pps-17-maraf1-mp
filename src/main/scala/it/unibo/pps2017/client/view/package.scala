
package it.unibo.pps2017.client

package object view {

  val MIN_HEIGHT: Int = 850
  val MIN_WIDTH: Int = 900

  /**
    * Trait to represent all possible stages handled by the GUI.
    * A stage defines a non ambiguous javafx scene reference, to make easier scene identification and handling.
    */
  sealed trait GUIStage

  case object LoginStage extends GUIStage

  case object SocialStage extends GUIStage

  case object GameStage extends GUIStage

  case object GenericStage extends GUIStage

}
