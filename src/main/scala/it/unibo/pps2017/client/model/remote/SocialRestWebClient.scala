
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.commons.remote.rest.API
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{GetAllOnlinePlayersAPI, RegisterSocialIDAPI, UnregisterSocialIDAPI}
import it.unibo.pps2017.server.model.OnlinePlayersMapEncoder
import it.unibo.pps2017.server.model.ServerApi.AddFriendAPI
import org.json4s.jackson.Serialization.read

/**
  * This class takes care of all the rest call executed by the socialcontroller structure of the architecture.
  */
class SocialRestWebClient(val socialController: SocialController, val discoveryContext: ServerContext)
  extends AbstractRestWebClient(discoveryContext) {

  override def executeAPICall(api: API.RestAPI, paramMap: Option[Map[String, Any]]): Unit = api match {
    case RegisterSocialIDAPI => invokeAPI(api, paramMap, registerAndUnregisterSocialIDCallBack, discoveryContext)
    case UnregisterSocialIDAPI => invokeAPI(api, paramMap, registerAndUnregisterSocialIDCallBack, discoveryContext)
    case GetAllOnlinePlayersAPI => invokeAPI(api, paramMap, getAllOnlinePlayersCallback, discoveryContext)
    case AddFriendAPI => invokeAPI(api, paramMap, addAFriendCallback, assignedServerContext.get)
  }

  /**
    * Callback for the RegisterSocialID and UnregisterSocialID API.
    *
    * @param responseBody the body of the response.
    */
  private def registerAndUnregisterSocialIDCallBack(responseBody: Option[String]): Unit =
    socialController.notifyCallResultToGUI(responseBody)


  /**
    * Callback for the GetAllOnlinePlayers API.
    *
    * @param responseBody the body of the response.
    */
  private def getAllOnlinePlayersCallback(responseBody: Option[String]): Unit = {
    val playerMap = read[OnlinePlayersMapEncoder](responseBody.get).map
    socialController.setOnlinePlayerList(playerMap)
  }

  private def addAFriendCallback(responseBody: Option[String]): Unit = {
    socialController.notifyCallResultToGUI(responseBody)
  }
}

object SocialRestWebClient {
  def apply(socialController: SocialController, discoveryContext: ServerContext): RestWebClient =
    new SocialRestWebClient(socialController, discoveryContext)
}
