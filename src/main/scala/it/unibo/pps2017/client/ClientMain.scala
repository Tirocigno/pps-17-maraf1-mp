
package it.unibo.pps2017.client

import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.view.GuiStack
import it.unibo.pps2017.commons.remote.game.MatchNature.CasualMatch
import it.unibo.pps2017.discovery.DiscoveryMain.args
import javafx.application.Application
import javafx.stage.Stage
import org.rogach.scallop.ScallopConf

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
    val conf = new Conf(args)
    clientController.startActorSystem(conf.discoveryip(), conf.myip())
    clientController.createRestClient(conf.discoveryip(), conf.myport())
    clientController.sendMatchRequest(CasualMatch, None)
  }
}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val discoveryip = opt[String]()
  val myip = opt[String]()
  val myport = opt[Int]()
  verify()
}

object ClientMain extends App {
    Application.launch(classOf[ClientMain], args: _*)
}
