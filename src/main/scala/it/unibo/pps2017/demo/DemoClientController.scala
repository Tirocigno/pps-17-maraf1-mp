
package it.unibo.pps2017.demo

import akka.actor.{ActorRef, ActorSystem, Props}

class DemoClientController {

  val system = ActorSystem("mySystem")
  /**
    * ActorRef of the actor*/
  val myActor: ActorRef = system.actorOf(Props(new DemoActor(this)))
  /**
    * Oggetto GUI
    */
  var gui = DemoGui(this)

  /**
    * Aggiorna la GUI.
    */
  def updateGUI(): Unit = gui.updateGUI()

  /**
    * Manda un messaggio all'attore.
    */
  def sendGuiEventMsg(): Unit = myActor ! FROMGUIMESSAGE

  /**
    * Questo metodo chiama un evento a caso sulla gui.
    */
  def triggerGuiEvent(): Unit = gui.clickedButton()

  /**
    * Questo metodo invia un messaggio all'attore col compito di aggiornare la GUI.
    */
  def triggerActorMessage(): Unit = myActor ! TOGUIMESSAGE

}
