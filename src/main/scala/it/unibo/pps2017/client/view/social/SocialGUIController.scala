
package it.unibo.pps2017.client.view.social

import it.unibo.pps2017.client.view.GUIController

trait SocialGUIController extends GUIController {

  /**
    * Open a new alter notifying an error.
    *
    * @errorToNotify a string containing the error message.
    */
  def notifyErrorOccurred(errorToNotify: String)

  /**
    * Update the list of current online friends
    *
    * @param friendList a list containing all online friends id.
    */
  def updateOnlineFriendsList(friendList: List[String])

  /**
    * Update the list of current online players.
    *
    * @param playersList a list containing all online players id.
    */
  def updateOnlinePlayersList(playersList: List[String])

  /**
    * Update player's current party.
    *
    * @param partyMap a map containing party member's role and ID.
    */
  def updateParty(partyMap: Map[String, String])

  /**
    * Notify on GUI the response to a call made by the user.
    *
    * @param sender         the user who send the response.
    * @param responseResult response result, positive if the sender has accepted the request, false otherwise.
    * @param request        the original request made by the user.
    */
  def notifyMessageResponse(sender: Option[String], responseResult: String, request: String)
}
