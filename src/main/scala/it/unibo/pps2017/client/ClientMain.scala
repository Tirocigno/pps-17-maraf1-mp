
package it.unibo.pps2017.client

import it.unibo.pps2017.client.controller.ClientController
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.view.GuiStack
import javafx.application.Application
import javafx.stage.Stage

class ClientMain extends Application {
  val matchController = new GameController()
  val clientController: ClientController = ClientController.getSingletonController

  override def start(primaryStage: Stage): Unit = {
    GuiStack().checkAndSetStage(primaryStage)
    //GuiStack().setCurrentScene(GameStage, matchController)
    startFoundGameRequest()
    primaryStage.show()
  }

  private def startFoundGameRequest(): Unit = {
    clientController.startActorSystem("127.0.0.1", "127.0.0.1")
    clientController.createRestClient("127.0.0.1", 2000)
    clientController.sendMatchRequest()
  }
}

object ClientMain extends App {
    Application.launch(classOf[ClientMain], args: _*)
}
