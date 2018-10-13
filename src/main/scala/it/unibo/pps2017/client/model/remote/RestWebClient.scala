
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.discovery.ServerContext

trait RestWebClient {

  val discoveryServerContext: ServerContext
  var assignedServerContext: ServerContext

  def getServer()

  def callRemoteAPI()

}
