
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.commons.remote.rest.API
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{GetAllOnlinePlayersAPI, RegisterSocialIDAPI, UnregisterSocialIDAPI}
import it.unibo.pps2017.server.model.OnlinePlayersMapEncoder
import org.json4s.jackson.Serialization.read

/**
  * This class takes care of all the rest call executed by the social structure of the architecture.
  */
class SocialRestWebClient(val socialController: SocialController, val discoveryContext: ServerContext)
  extends AbstractRestWebClient(discoveryContext) {

  override def executeAPICall(api: API.RestAPI, paramMap: Option[Map[String, Any]]): Unit = api match {
    case RegisterSocialIDAPI => invokeAPI(api, paramMap, registerAndUnregisterSocialIDCallBack, discoveryContext)
    case UnregisterSocialIDAPI => invokeAPI(api, paramMap, registerAndUnregisterSocialIDCallBack, discoveryContext)
    case GetAllOnlinePlayersAPI => invokeAPI(api, paramMap, getAllOnlinePlayersCallback, discoveryContext)
  }

  /**
    * Callback for the RegisterSocialID and UnregisterSocialID API.
    *
    * @param responseBody the body of the response.
    */
  private def registerAndUnregisterSocialIDCallBack(responseBody: Option[String]): Unit =
    socialController.notifyCallResultToGUI(responseBody.get)


  /**
    * Callback for the GetAllOnlinePlayers API.
    *
    * @param responseBody the body of the response.
    */
  private def getAllOnlinePlayersCallback(responseBody: Option[String]): Unit = {
    val playerMap = read[OnlinePlayersMapEncoder](responseBody.get).map
    socialController.setAndDisplayOnlinePlayerList(playerMap)
  }
}

object SocialRestWebClient {
  def apply(socialController: SocialController, discoveryContext: ServerContext): RestWebClient =
    new SocialRestWebClient(socialController, discoveryContext)
}
