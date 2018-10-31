
package it.unibo.pps2017.client.view

import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

trait GuiStack {

  /**
    * Set the current scene inside GUI.
    *
    * @param stage the stage on which the GUI need to enter.
    */
  def setCurrentScene(stage: GUIStage)

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

}

object GuiStack {

  private val singletonStack: GuiStack = new GuiStackImpl()

  def apply(): GuiStack = singletonStack

  private class GuiStackImpl() extends GuiStack {

    var sceneMap: Map[GUIStage, Scene] = Map()
    var mainStage: Option[Stage] = None
    var previousScene: Option[Scene] = None


    override def setCurrentScene(stage: GUIStage): Unit = {
      val sceneToSet = sceneMap.getOrElse(stage, throw new NoSuchElementException())
      if (mainStage.get.getScene != null) {
        previousScene = Some(mainStage.get.getScene)
      }
      switchScene(sceneToSet)
    }

    override def addStage(stage: GUIStage, scene: Scene): Unit = sceneMap += (stage -> scene)

    override def restorePreviousScene(): Unit = switchScene(previousScene.get)

    private def switchScene(scene: Scene): Unit = {
      runLater(() => {
        mainStage.get.setScene(scene)
      })
    }

    private def runLater(strategyToRunLater: () => Unit): Unit = Platform.runLater(() => {
      strategyToRunLater()
    })

    override def checkAndSetStage(stage: Stage): Unit = mainStage match {
      case None => mainStage = Some(stage)
      case Some(_) =>
    }
  }

}
