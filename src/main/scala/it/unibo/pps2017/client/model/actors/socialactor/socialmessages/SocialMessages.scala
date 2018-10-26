package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, SocialMap}


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

}
