
package it.unibo.pps2017.client

package object view {

  /**
    * Trait to represent all possible stages handled by the GUI.
    */
  sealed trait GUIStage

  case object LoginStage extends GUIStage

  case object SocialStage extends GUIStage

  case object GameStage extends GUIStage

  case object GenericStage extends GUIStage

}
