
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.commons.remote.rest.API
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterSocialIDAPI
import it.unibo.pps2017.server.model.ServerApi.{AddFriendAPI, GetUserAPI}
import it.unibo.pps2017.server.model.{User, UserFriends}
import org.json4s.jackson.Serialization.read

/**
  * This class takes care of all the rest call executed by the socialcontroller structure of the architecture.
  */
class SocialRestWebClient(val socialController: SocialController, val discoveryContext: ServerContext)
  extends AbstractRestWebClient(discoveryContext) {

  override def executeAPICall(api: API.RestAPI, paramMap: Option[Map[String, Any]], parameterPath: String): Unit = api match {
    case RegisterSocialIDAPI => invokeAPI(api, paramMap, registerAndUnregisterSocialIDCallBack, discoveryContext)
    case AddFriendAPI => invokeAPI(api, paramMap, addAFriendCallBack, assignedServerContext.get,
      AddFriendAPI.path.replace(AddFriendAPI.parameterPath, parameterPath))
    case GetUserAPI => invokeAPI(api, paramMap, getUserCallBack, assignedServerContext.get,
      GetUserAPI.path.replace(GetUserAPI.parameterPath, parameterPath))
  }

  /**
    * Callback for the RegisterSocialID and UnregisterSocialID API.
    *
    * @param responseBody the body of the response.
    */
  private def registerAndUnregisterSocialIDCallBack(responseBody: Option[String]): Unit =
    println(responseBody.get)

  private def getAllFriendsCallBack(responseBody: Option[String]): Unit = {
    val friendList = read[UserFriends](responseBody.get).friends.toList
    socialController.setFriendsList(friendList)
  }

  private def addAFriendCallBack(responseBody: Option[String]): Unit = {
    socialController.notifyCallResultToGUI(responseBody)
  }

  private def getUserCallBack(responseBody: Option[String]): Unit = {
    val scores = read[User](responseBody.get).score
    socialController.setScoreInsideGUI(scores)
  }
}

object SocialRestWebClient {
  def apply(socialController: SocialController, discoveryContext: ServerContext): RestWebClient =
    new SocialRestWebClient(socialController, discoveryContext)
}
