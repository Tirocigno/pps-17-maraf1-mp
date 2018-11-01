
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.commons.remote.rest.API
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.server.model.GameFound
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI
import org.json4s.jackson.Serialization.read

/**
  * Implementation of the abstract rest web client used for gaming purpouse.
  *
  * @param discoveryServerContext the discovery server coordinates.
  */
class GameRestWebClient(discoveryServerContext: ServerContext) extends AbstractRestWebClient(discoveryServerContext) {

  override def executeAPICall(api: API.RestAPI, paramMap: Option[Map[String, Any]]): Unit = api match {
    case FoundGameRestAPI => invokeAPI(api, paramMap, foundGameCallBack, assignedServerContext.get)

  }

  /**
    * Handler for the FoundGame API response.
    *
    * @param jSonSource the body of the response.
    */
  private def foundGameCallBack(jSonSource: Option[String]): Unit = {
    val gameID = read[GameFound](jSonSource.get).gameId
    clientController.setGameID(gameID)
  }

}

object GameRestWebClient {
  def apply(discoveryServerContext: ServerContext): RestWebClient = new GameRestWebClient(discoveryServerContext)
}