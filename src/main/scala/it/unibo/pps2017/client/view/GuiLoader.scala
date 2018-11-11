
package it.unibo.pps2017.client.view

import it.unibo.pps2017.client.controller.Controller
import it.unibo.pps2017.client.controller.actors.playeractor.MatchController
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.view.game.GameGUIController
import it.unibo.pps2017.client.view.login.LoginGUIController
import it.unibo.pps2017.client.view.social.SocialGUIController
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}

/**
  * Loader class for all the GUI scenes.
  *
  */
class GuiLoader() {

  private val stack: GuiStack = GuiStack()

  /**
    * Deploy a new scene and bind it to a controller.
    *
    * @param controllerToBind the controller to bind to the GUI.
    * @return the deployed scene to set inside GUI.
    */
  def deployGuiStage(controllerToBind: Controller, stage: GUIStage): Scene = controllerToBind match {
    case controller: ClientController if stage == LoginStage =>
      createAndRegisterScene(GuiLoader.LOGIN_SCENE_FXML, controller, stage)
    case controller: ClientController if stage == GenericStage =>
      createAndRegisterScene(GuiLoader.GENERIC_SCENE_FXML, controller, stage)
    case controller: MatchController =>
      createAndRegisterScene(GuiLoader.GAME_SCENE_FXML, controller, stage)
    case controller: SocialController =>
      createAndRegisterScene(GuiLoader.SOCIAL_SCENE_FXML, controller, stage)
    case _ => throw new IllegalArgumentException()
  }


  /**
    * Create a scene and register it inside the GUIStack
    *
    * @param fxmlPath   path of fxml file to load.
    * @param controller controller to bind to created scene.
    * @param stage      the stage key for the scene to be registered inside GUIStack.
    * @return a scene built upon these parameters.
    */
  private def createAndRegisterScene(fxmlPath: String, controller: Controller, stage: GUIStage): Scene = {
    val loader = new FXMLLoader(classOf[GuiLoader].getResource(fxmlPath))
    println(loader)
    val root: Parent = loader.load()
    val scene = new Scene(root)
    stack.addStage(stage, scene)
    val guiController: GUIController = loader.getController()
    bindControllers(controller, guiController)
    scene
  }

  /**
    * Bind a gui controller to a model controller.
    *
    * @param controller    the model controller to be bound.
    * @param guiController the javafx controller to be bound.
    */
  private def bindControllers(controller: Controller, guiController: GUIController): Unit = controller match {
    case controller: SocialController =>
      val castedGui = guiController.asInstanceOf[SocialGUIController]
      controller.setCurrentGui(castedGui)
      castedGui.setController(controller)
    case controller: MatchController =>
      val castedGui = guiController.asInstanceOf[GameGUIController]
      controller.setCurrentGui(castedGui)
      castedGui.setController(controller)
    case controller: ClientController if guiController.isInstanceOf[GenericGUIController] =>
      val castedGui = guiController.asInstanceOf[GenericGUIController]
      controller.setCurrentGUI(castedGui)
      castedGui.setController(controller)
    case controller: ClientController if guiController.isInstanceOf[LoginGUIController] =>
      val castedGui = guiController.asInstanceOf[LoginGUIController]
      controller.setCurrentGUI(castedGui)
      castedGui.setController(controller)
  }



}

object GuiLoader {

  val LOGIN_SCENE_FXML = "registration.fxml"
  val SOCIAL_SCENE_FXML = "socialView.fxml"
  val GAME_SCENE_FXML = "gameStage.fxml"
  val GENERIC_SCENE_FXML = "genericView.fxml"
}
