
package it.unibo.pps2017.client

import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.view.GuiStack
import it.unibo.pps2017.commons.remote.game.MatchNature.CasualMatch
import javafx.application.Application
import javafx.stage.Stage
import org.rogach.scallop.ScallopConf

class ClientMain extends Application {

  override def start(primaryStage: Stage): Unit = {
    GuiStack().checkAndSetStage(primaryStage)
    //GuiStack().setCurrentScene(GameStage, matchController)
    primaryStage.show()
  }

}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val discoveryip = opt[String]()
  val myip = opt[String]()
  val myport = opt[Int]()
  verify()
}

object ClientMain extends App {
  val conf = new Conf(args)
  if (conf.discoveryip.supplied) {
    println("yay")
  }
  val clientController: ClientController = ClientController.getSingletonController
  clientController.startActorSystem(conf.discoveryip(), conf.myip())
  clientController.createRestClient(conf.discoveryip(), conf.myport())
  clientController.sendMatchRequest(CasualMatch, None)
    Application.launch(classOf[ClientMain], args: _*)

}
