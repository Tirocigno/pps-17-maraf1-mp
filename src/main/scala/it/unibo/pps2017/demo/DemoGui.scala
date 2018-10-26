
package it.unibo.pps2017.demo

case class DemoGui(demoClientController: DemoClientController) {
  def clickedButton(): Unit = demoClientController.sendGuiEventMsg()

  def updateGUI(): Unit = println("GUI was updated!")
}
