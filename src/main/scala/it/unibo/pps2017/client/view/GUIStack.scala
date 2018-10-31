
package it.unibo.pps2017.client.view

import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

trait GUIStack {

  /**
    * Set the currentStage inside GUI.
    *
    * @param stage the stage on which the GUI need to enter.
    */
  def setCurrentStage(stage: GUIStage)

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
    * Set application current stage.
    *
    * @param stage the currentStage of the application
    */
  def setStage(stage: Stage)
}

object GUIStack {

  private val singletonStack: GUIStack = new GUIStackImpl()

  def apply(): GUIStack = singletonStack

  private class GUIStackImpl() extends GUIStack {

    var sceneMap: Map[GUIStage, Scene] = Map()
    var mainStage: Option[Stage] = None
    var previousScene: Option[Scene] = None


    override def setCurrentStage(stage: GUIStage): Unit = {
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

    override def setStage(stage: Stage): Unit = this.mainStage = Some(stage)
  }

}
