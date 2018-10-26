
package it.unibo.pps2017.demo

import akka.actor.Actor
import akka.event.Logging

class DemoActor(demoClientController: DemoClientController) extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case FROMGUIMESSAGE => log.info("Actor received message from GUI!")
    case TOGUIMESSAGE => demoClientController.updateGUI()
    case _ => log.info("received unknown message")
  }

}
