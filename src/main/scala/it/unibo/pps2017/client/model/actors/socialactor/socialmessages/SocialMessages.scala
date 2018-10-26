
package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PartnerReference, PlayerID, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}


/**
  * This object will contains all the possible messages sent or received by the socialActor
  */
object SocialMessages {

  sealed trait SocialMessage extends ActorMessage

  /**
    * Message to set a new PlayerOnlineMap inside the Actor.
    *
    * @param socialMap the map of current online players.
    */
  case class SetOnlinePlayersMapMessage(socialMap: SocialMap)

  /**
    * Message to set the current friend list.
    *
    * @param friendsList the list of player's friend pulled from database.
    */
  case class SetFriendsList(friendsList: FriendList)

  /**
    * Tell the actor to send a friend request message to a specific player.
    *
    * @param friendID the id of the player to add as a friend.
    */
  case class TellAddFriendRequestMessage(friendID: PlayerID)

  /**
    * Message to send to a player to ask if we can add him to our friends.
    *
    * @param senderID the request sender id.
    */
  case class AddFriendRequestMessage(senderID: PlayerID)

  /**
    * Tell the actor to send a response message for a friend request.
    *
    * @param socialResponse the response of the player.
    */
  case class TellAddFriendResponseMessage(socialResponse: SocialResponse, sender: PlayerID)

  /**
    * Message to send a response for a friendship request.
    *
    * @param socialResponse the response of the player.
    */
  case class AddFriendResponseMessage(socialResponse: SocialResponse)

  /**
    * Tell the actor to invite a player on a match with a specified roles.
    *
    * @param playerID the id of the player to invite.
    * @param role     the role on which the player will play.
    */
  case class TellInvitePlayerRequestMessage(playerID: PlayerID, role: PartyRole)

  /**
    * Message to send a request on joining a match with a specified role
    *
    * @param senderID the sender player's id.
    * @param role     the role on which the receiver will play.
    */
  case class InvitePlayerRequestMessage(senderID: PlayerID, role: PartyRole)

  /**
    * Tell the actor to send a response for a Invite request.
    *
    * @param socialResponse the response to send.
    */
  case class TellInvitePlayerResponseMessage(socialResponse: SocialResponse)

  /**
    * Response message for a Invite request.
    *
    * @param socialResponse the response to send
    * @param role           the role of the request
    * @param myRole         the roles on which the player will play
    * @param partnerRole    the information about the partner of the player.
    */
  case class InvitePlayerResponseMessage(socialResponse: SocialResponse, role: PartyRole,
                                         myRole: Option[PartyPlayer], partnerRole: Option[PartnerReference])



}
