
package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, PlayerReference}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}


/**
  * This object will contains all the possible messages sent or received by the socialActor
  */
object SocialMessages {

  /**
    * Trait to identify the social messages.
    */
  sealed trait SocialMessage extends ActorMessage

  /**
    * Trait to identify the request messages between all social messages
    */
  sealed trait RequestMessage extends SocialMessage

  /**
    * Trait to identify all the response messages between all social messages.
    */
  sealed trait ResponseMessage extends SocialMessage {
    /**
      * Define request corresponding to the response.
      *
      * @return a string containing the request.
      */
    def request: String
  }

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
  case class AddFriendResponseMessage(socialResponse: SocialResponse, senderID: PlayerID) extends ResponseMessage {
    override def request: String = "Friend Request"
  }

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
  case class InvitePlayerResponseMessage(socialResponse: SocialResponse, myRole: PartyPlayer,
                                         partnerRole: Option[PlayerReference]) extends ResponseMessage {
    override def request: String = "Invite Request"
  }

  /**
    * Notify to all the party the game id of the match to play.
    *
    * @param gameID a string containing the gameid.
    */
  case class NotifyGameIDMessage(gameID: String) extends SocialMessage

  /**
    * Message containing the id of a game joined by the leader.
    *
    * @param gameID a string containing the gameID.
    */
  case class GameIDMessage(gameID: String) extends SocialMessage

  /**
    * Fetch the party inside the actor and start a game passing the party as parameters.
    */
  case object GetPartyAndStartGameMessage extends SocialMessage

  /**
    * This message unstash all the pending messages inside an actor.
    */
  case object UnstashAllMessages extends SocialMessage

  /**
    * Message to reset the internal party.
    */
  case object ResetParty extends SocialMessage

  /**
    * Tell the actor to kill himself.
    */
  case object KillYourSelf extends SocialMessage



}
