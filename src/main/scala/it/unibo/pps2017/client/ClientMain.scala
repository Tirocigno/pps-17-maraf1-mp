
package it.unibo.pps2017.client

import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.view.{GameStage, GuiStack}
import javafx.application.Application
import javafx.stage.Stage

class ClientMain extends Application {
  val matchController = new GameController()

  override def start(primaryStage: Stage): Unit = {
    GuiStack().checkAndSetStage(primaryStage)
    GuiStack().setCurrentScene(GameStage, matchController)
    primaryStage.show()
  }
}

object ClientMain {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ClientMain], args: _*)
  }
}
