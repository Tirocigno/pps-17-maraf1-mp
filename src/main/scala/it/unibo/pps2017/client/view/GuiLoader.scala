
package it.unibo.pps2017.client.view

import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.controller.{ClientController, Controller, MatchController}
import it.unibo.pps2017.client.view.game.GameGUIController
import it.unibo.pps2017.client.view.social.SocialGUIController
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}

/**
  * Loader class for all the GUI scenes.
  *
  */
class GuiLoader() {

  val stack: GuiStack = GuiStack()

  def deployGuiStage(controllerToBind: Controller): Unit = controllerToBind match {
    case controller: ClientController =>
    case controller: MatchController =>
    case controller: SocialController =>
    case _ => throw new IllegalArgumentException()
  }


  private def createAndRegisterScene(fxmlPath: String, cssPath: String, controller: Controller, stage: GUIStage): Unit = {
    val loader = new FXMLLoader(classOf[GuiLoader].getResource(fxmlPath))
    val root: Parent = loader.load()
    val scene = new Scene(root)
    scene.getStylesheets.add(getClass.getResource(cssPath).toExternalForm)
    stack.addStage(stage, scene)
  }

  private def bindControllers(controller: Controller, guiController: Nothing): Unit = controller match {
    case controller: SocialController =>
      val castedGui = guiController.asInstanceOf[SocialGUIController]
      controller.setCurrentGui(castedGui)
      castedGui.setController(controller)
    case controller: MatchController =>
      val castedGui = guiController.asInstanceOf[GameGUIController]
      controller.setCurrentGui(castedGui)
      castedGui.setController(controller)
    case controller: ClientController =>
      val castedGui = guiController.asInstanceOf[GenericGUIController]
      controller.setCurrentGUI(castedGui)
      castedGui.setController(controller)
  }



}

object GuiLoader {

  val MAIN_SCENE_FXML = ""
  val MAIN_SCENE_CSS = ""
  val SOCIAL_SCENE_FXML = ""
  val SOCIAL_SCENE_CSS = ""
  val GAME_SCENE_FXML = "gameStage.fxml"
  val GAME_SCENE_CSS = "gameCSS.css"
}
