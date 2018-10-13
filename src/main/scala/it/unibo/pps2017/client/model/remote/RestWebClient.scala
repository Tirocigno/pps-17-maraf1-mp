
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.utils.remote.RestUtils.ServerContext


trait RestWebClient {

  val discoveryServerContext: ServerContext
  var assignedServerContext: ServerContext

  def retrieveRemoteServer()

  def callRemoteAPI()

}
