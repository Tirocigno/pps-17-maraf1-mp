
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerReference, SocialMap}
import it.unibo.pps2017.discovery.structures.SocialActorsMap

trait SocialPlayersMap extends SocialActorsMap {

  def setOnlinePlayerList(playersList: List[PlayerReference]): Unit

  def setFriendsList(friendList: FriendList): Unit

  def updateFriendList(friendID: String): Unit

  def getAllOnlineStrangers: FriendList

  def getAllOnlineFriends: FriendList
}

object SocialPlayersMap {
  def apply: SocialPlayersMap = new SocialPlayersMapImpl()

  private class SocialPlayersMapImpl() extends SocialPlayersMap {
    val socialActorsMap: SocialActorsMap = SocialActorsMap()
    var friendList: FriendList = List()

    override def getCurrentOnlinePlayerMap: SocialMap = socialActorsMap.getCurrentOnlinePlayerMap

    override def registerUser(userID: String, socialActorRef: ActorRef): Unit =
      socialActorsMap.registerUser(userID, socialActorRef)

    override def unregisterUser(userID: String): Unit = socialActorsMap.unregisterUser(userID)

    override def setOnlinePlayerList(playersList: List[PlayerReference]): Unit =
      playersList.foreach(player => socialActorsMap.registerUser(player.playerID, player.playerRef))

    override def setFriendsList(friendList: FriendList): Unit =
      friendList.foreach(this.friendList += _)

    override def updateFriendList(friendID: String): Unit =
      friendList += friendID

    override def getAllOnlineFriends: FriendList =
      friendList.filter(friendID =>
        socialActorsMap.getCurrentOnlinePlayerMap.keySet.exists(_.equals(friendID)))

    override def getAllOnlineStrangers: FriendList =
      socialActorsMap.getCurrentOnlinePlayerMap.keySet
        .filter(playerID => !friendList.exists(playerID.equals(_)))
        .toList
  }

}
