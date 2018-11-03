
package it.unibo.pps2017.client

import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.view.GuiStack
import javafx.application.Application
import javafx.stage.Stage
import org.rogach.scallop.ScallopConf

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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


  akka.actor.ActorSystem("Akka", ConfigFactory.load("redisConf"))
    .scheduler.scheduleOnce(1 second) {

    val conf = new Conf(args)
    if (conf.discoveryip.supplied) {
    }
    val clientController: ClientController = ClientController.getSingletonController
    clientController.startActorSystem(conf.discoveryip(), conf.myip())
    clientController.createRestClient(conf.discoveryip(), conf.myport())
    /*clientController.sendMatchRequest(CasualMatch, None) */
    clientController.startMatchWatching("1541256231119")

  }


  Application.launch(classOf[ClientMain], args: _*)

}
