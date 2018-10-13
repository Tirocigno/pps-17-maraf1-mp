
package it.unibo.pps2017.client.controller

trait ClientController {

  def notifyError(throwable: Throwable)

}

object SingletonClientController extends ClientController {
  override def notifyError(throwable: Throwable): Unit = ???
}
