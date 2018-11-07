package it.unibo.pps2017.commons.remote.social

import akka.actor.ActorRef

object SocialUtils {

  type PlayerID = String

  type SocialMap = Map[String, ActorRef]

  type FriendList = List[String]

  /**
    * Class to bind together a player username and actorRef.
    *
    * @param playerID  the player username.
    * @param playerRef the actorRef of that actor.
    */
  case class PlayerReference(playerID: PlayerID, playerRef: ActorRef)

}
