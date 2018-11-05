
package it.unibo.pps2017.client

import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.view.{GuiStack, LoginStage}
import javafx.application.Application
import javafx.stage.Stage
import org.rogach.scallop.ScallopConf

class ClientMain extends Application {

  override def start(primaryStage: Stage): Unit = {
    GuiStack().checkAndSetStage(primaryStage)
    GuiStack().setCurrentScene(LoginStage, ClientController.getSingletonController)
    primaryStage.show()
    primaryStage.centerOnScreen()
    primaryStage.setOnCloseRequest(_ => {
      System.exit(0)
    })
  }
}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val discoveryip = opt[String]()
  val currentip = opt[String]()
  val discoveryport = opt[Int]()
  verify()
}

object ClientMain extends App {
  var currentip = "127.0.0.1"
  var discoveryip = currentip
  var discoveryport = 2000
  val conf = new Conf(args)
  if (conf.discoveryip.supplied) discoveryip = conf.discoveryip()
  if (conf.currentip.supplied) currentip = conf.currentip()
  if (conf.discoveryport.supplied) discoveryport = conf.discoveryport()
  val clientController: ClientController = ClientController.getSingletonController
  clientController.startActorSystem(discoveryip, currentip)
  clientController.createRestClient(discoveryip, discoveryport)
    Application.launch(classOf[ClientMain], args: _*)
}
