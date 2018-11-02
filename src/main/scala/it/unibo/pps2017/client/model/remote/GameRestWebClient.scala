
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.commons.remote.rest.API
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetAllMatchesAPI
import it.unibo.pps2017.server.model.ServerApi.{FoundGameRestAPI, GameRestAPI, LoginAPI}
import it.unibo.pps2017.server.model.{Game, GameFound, MatchesSetEncoder}
import org.json4s.jackson.Serialization.read

/**
  * Implementation of the abstract rest web client used for gaming purpouse.
  *
  * @param discoveryServerContext the discovery server coordinates.
  */
class GameRestWebClient(discoveryServerContext: ServerContext) extends AbstractRestWebClient(discoveryServerContext) {

  override def executeAPICall(api: API.RestAPI, paramMap: Option[Map[String, Any]], parameterPath: String): Unit =
    api match {
    case FoundGameRestAPI => invokeAPI(api, paramMap, foundGameCallBack, assignedServerContext.get)
    case GameRestAPI => invokeAPI(api, paramMap, gameCallBack, assignedServerContext.get)
    case GetAllMatchesAPI => invokeAPI(api, paramMap, getAllMatchesApiCallBack, discoveryServerContext)
    case LoginAPI => invokeAPI(api, paramMap, loginCallBack, assignedServerContext.get, parameterPath)
  }

  /**
    * Handler for the FoundGame API response.
    *
    * @param jSonSource the body of the response.
    */
  private def foundGameCallBack(jSonSource: Option[String]): Unit = {
    val gameID = read[GameFound](jSonSource.get).gameId
    clientController.handleMatchResponse(gameID)
  }

  private def loginCallBack(jSonSource: Option[String]): Unit = {
    clientController.handleLoginAndRegistrationResponse()
  }


  /**
    * Handler for the Game API response.
    *
    * @param jSonSource the body of the response.
    */
  private def gameCallBack(jSonSource: Option[String]): Unit = {
    val game = read[Game](jSonSource.get)
    clientController.handleMatchReplay(game)
  }

  /**
    * Handler for GetAllMatchesAPI response.
    *
    * @param jSonSource the body of the response.
    */
  private def getAllMatchesApiCallBack(jSonSource: Option[String]): Unit = {
    val matchesList = read[MatchesSetEncoder](jSonSource.get).set.toList
    clientController.displayCurrentMatchesList(matchesList)
  }

}

object GameRestWebClient {
  def apply(discoveryServerContext: ServerContext): RestWebClient = new GameRestWebClient(discoveryServerContext)
}
