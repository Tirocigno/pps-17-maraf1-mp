
package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, PlayerReference, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}


/**
  * This object will contains all the possible messages sent or received by the socialActor
  */
object SocialMessages {

  type RequestClass = String

  sealed trait SocialMessage extends ActorMessage

  sealed trait RequestMessage extends SocialMessage {
    def request: RequestClass
  }

  /**
    * Message to set a new PlayerOnlineMap inside the Actor.
    *
    * @param socialMap the map of current online players.
    */
  case class SetOnlinePlayersMapMessage(socialMap: SocialMap) extends SocialMessage

  /**
    * Message to set the current friend list.
    *
    * @param friendsList the list of player's friend pulled from database.
    */
  case class SetFriendsList(friendsList: FriendList) extends SocialMessage

  /**
    * Tell the actor to send a friend request message to a specific player.
    *
    * @param friendID the id of the player to add as a friend.
    */
  case class TellAddFriendRequestMessage(friendID: PlayerID) extends SocialMessage

  /**
    * Message to send to a player to ask if we can add him to our friends.
    *
    * @param senderID the request sender id.
    */
  case class AddFriendRequestMessage(sender: PlayerReference) extends RequestMessage {
    override def request: RequestClass = "ADD_FRIEND"
  }

  /**
    * Tell the actor to send a response message for a friend request.
    *
    * @param socialResponse the response of the player.
    */
  case class TellAddFriendResponseMessage(socialResponse: SocialResponse, sender: PlayerID) extends SocialMessage

  /**
    * Message to send a response for a friendship request.
    *
    * @param socialResponse the response of the player.
    */
  case class AddFriendResponseMessage(socialResponse: SocialResponse) extends SocialMessage

  /**
    * Tell the actor to invite a player on a match with a specified roles.
    *
    * @param playerID the id of the player to invite.
    * @param role     the role on which the player will play.
    */
  case class TellInvitePlayerRequestMessage(playerID: PlayerID, role: PartyRole) extends SocialMessage

  /**
    * Message to send a request on joining a match with a specified role
    *
    * @param senderID the sender player's id.
    * @param role     the role on which the receiver will play.
    */
  case class InvitePlayerRequestMessage(sender: PlayerReference, role: PartyRole) extends RequestMessage {
    override def request: RequestClass = "INVITE_PLAYER"
  }

  /**
    * Tell the actor to send a response for a Invite request.
    *
    * @param socialResponse the response to send.
    */
  case class TellInvitePlayerResponseMessage(socialResponse: SocialResponse) extends SocialMessage

  /**
    * Response message for a Invite request.
    *
    * @param socialResponse the response to send
    * @param role           the role of the request
    * @param myRole         the roles on which the player will play
    * @param partnerRole    the information about the partner of the player.
    */
  case class InvitePlayerResponseMessage(socialResponse: SocialResponse, myRole: Option[PartyPlayer],
                                         partnerRole: Option[PlayerReference]) extends SocialMessage

  /**
    * Notify to all the party the game id of the match to play.
    *
    * @param gameID a string containing the gameid.
    */
  case class NotifyGameIDMessage(gameID: String)

  /**
    * Fetch the party inside the actor and start a game passing the party as parameters.
    */
  case object GetPartyAndStartGameMessage extends SocialMessage



}
