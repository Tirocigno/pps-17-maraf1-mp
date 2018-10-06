
package it.unibo.pps2017.demo

object ScalaDemo extends App {
  val clientController = new DemoClientController
  clientController.triggerActorMessage()
  clientController.triggerGuiEvent()
}
