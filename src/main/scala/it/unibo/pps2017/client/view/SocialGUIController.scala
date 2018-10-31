package it.unibo.pps2017.client.view

trait SocialGUIController {

  /**
    * Open a new alert notifying an error.
    *
    * @param errorToNotify a string containing the error message.
    */
  def notifyErrorOccurred(errorToNotify: String)

  /**
    * Update the list of current online friends
    *
    * @param friendList a list containing all online friends id.
    */
  def updateOnlineFriendsList(friendList: java.util.List[String])

  /**
    * Update the list of current online players.
    *
    * @param playersList a list containing all online players id.
    */
  def updateOnlinePlayersList(playersList: java.util.List[String])

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
  def notifyMessageResponse(sender: String, responseResult: String, request: String)

  /**
    * Show the user a friend/party request received.
    *
    * @param sender the player who sent the request.
    * @param role   the role on which the sender want the player to play as.
    */
  def displayRequest(sender: String, role: String)

  /**
    * Notify to the user the result of a Rest Call.
    *
    * @param message the body of the request to display.
    */
  def notifyAPIResult(message: String)
}

