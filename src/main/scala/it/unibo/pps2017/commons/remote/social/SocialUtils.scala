package it.unibo.pps2017.commons.remote.social

import akka.actor.ActorRef

object SocialUtils {

  type PlayerID = String

  type SocialMap = Map[String, ActorRef]

  type FriendList = List[String]

  case class PartnerReference(partnerID: PlayerID, partnerRef: ActorRef)

}
