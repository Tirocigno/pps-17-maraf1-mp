
package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, PlayerReference}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}


/**
  * This object will contains all the possible messages sent or received by the socialActor
  */
object SocialMessages {


  sealed trait SocialMessage extends ActorMessage

  sealed trait RequestMessage extends SocialMessage

  /**
    * Message to set a new PlayerOnlineMap inside the Actor.
    *
    * @param playersList a list containing all the playersRef.
    */
  case class SetOnlinePlayersMapMessage(playersList: List[PlayerReference]) extends SocialMessage

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
    * @param sender the request sender id and actorRef.
    */
  case class AddFriendRequestMessage(sender: PlayerReference) extends RequestMessage
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
    * @param senderID       response sender's id.
    */
  case class AddFriendResponseMessage(socialResponse: SocialResponse, senderID: PlayerID) extends SocialMessage

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
    * @param sender the request sender id and actorRef.
    * @param role   the role on which the receiver will play.
    */
  case class InvitePlayerRequestMessage(sender: PlayerReference, role: PartyRole) extends RequestMessage

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
