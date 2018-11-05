package it.unibo.pps2017.client.view

import it.unibo.pps2017.client.controller.Controller
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

trait GuiStack {

  /**
    * Set the current scene inside GUI, if the scene is not present, it's automatically created and added.
    *
    * @param stage      the stage on which the GUI need to enter.
    * @param controller the controller to bind to the scene if it has to be created.
    */
  def setCurrentScene(stage: GUIStage, controller: Controller)

  /**
    * Add a scene to the stack.
    *
    * @param stage the scene stage
    * @param scene a javafx scene to load when the gui is required.
    */
  def addStage(stage: GUIStage, scene: Scene)

  /**
    * Remove the currentScene and restore the previous.
    */
  def restorePreviousScene()

  /**
    * Set application current stage if not already present.
    *
    * @param stage the currentStage of the application.
    */
  def checkAndSetStage(stage: Stage)

  /**
    * Getter for the stage.
    *
    * @return the stage on which the application is running.
    */
  def stage: Stage

}

object GuiStack {

  private val singletonStack: GuiStack = new GuiStackImpl()
  private val guiLoader: GuiLoader = new GuiLoader()

  def apply(): GuiStack = singletonStack

  private class GuiStackImpl() extends GuiStack {

    var sceneMap: Map[GUIStage, Scene] = Map()
    var mainStage: Option[Stage] = None
    var previousScene: Option[Scene] = None
    var previousStage: Option[GUIStage] = None
    var currentStage: Option[GUIStage] = None

    override def setCurrentScene(stage: GUIStage, controller: Controller): Unit = {
      Platform.runLater(() => {
        val sceneToSet = sceneMap.getOrElse(stage, loadScene(controller, stage))

        if (mainStage.get.getScene != null) {
          previousScene = Some(mainStage.get.getScene)
        }
        previousStage = currentStage
        currentStage = Some(stage)
        switchScene(sceneToSet, stage)
      })
    }

    override def addStage(stage: GUIStage, scene: Scene): Unit = sceneMap += (stage -> scene)

    override def restorePreviousScene(): Unit = {

      if (currentStage.get.equals(GameStage)) sceneMap = sceneMap - GameStage

      switchScene(previousScene.get, previousStage.get)
    }

    override def checkAndSetStage(stage: Stage): Unit = mainStage match {
      case None => mainStage = Some(stage)
      case Some(_) =>
    }

    override def stage: Stage = mainStage.get

    private def switchScene(scene: Scene, stage: GUIStage): Unit = {
      mainStage.get.setScene(scene)
      stage match {
        case GameStage => setGameStage()
        case _ => setOtherScene()
     }
    }

    private def loadScene(controller: Controller, stage: GUIStage): Scene = {
      guiLoader.deployGuiStage(controller, stage)
    }

    private def setGameStage(): Unit = {
      //mainStage.get.setFullScreen(true)
      //mainStage.get.setMinHeight(MIN_HEIGHT)
      //mainStage.get.setMinWidth(MIN_WIDTH)
      mainStage.get.setResizable(true)
      mainStage.get.centerOnScreen()
    }

    private def setOtherScene(): Unit = {
      mainStage.get.setFullScreen(false)
      mainStage.get.setResizable(false)
      mainStage.get.centerOnScreen()
    }
  }

}
