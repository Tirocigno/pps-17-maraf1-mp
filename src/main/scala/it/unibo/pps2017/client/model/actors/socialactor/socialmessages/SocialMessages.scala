package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}


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

}
