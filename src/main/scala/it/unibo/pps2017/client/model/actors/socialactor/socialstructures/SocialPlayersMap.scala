
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, PlayerReference, SocialMap}
import it.unibo.pps2017.discovery.structures.SocialActorsMap

trait SocialPlayersMap extends SocialActorsMap {

  def getPlayerID(friendID: PlayerID): ActorRef

  def setOnlinePlayerList(playersList: List[PlayerReference]): Unit

  def setFriendsList(friendList: FriendList): Unit

  def updateFriendList(friendID: PlayerID): Unit

  def getAllOnlineStrangers: FriendList

  def getAllOnlineFriends: FriendList

}

object SocialPlayersMap {
  def apply(playerID: PlayerID): SocialPlayersMap = new SocialPlayersMapImpl(playerID)

  private class SocialPlayersMapImpl(val playerID: PlayerID) extends SocialPlayersMap {
    val socialActorsMap: SocialActorsMap = SocialActorsMap()
    var friendList: FriendList = List()

    override def getPlayerID(friendID: PlayerID): ActorRef =
      socialActorsMap.getCurrentOnlinePlayerMap.getOrElse(friendID, throw new NoSuchElementException())

    override def getCurrentOnlinePlayerMap: SocialMap = socialActorsMap.getCurrentOnlinePlayerMap

    override def setOnlinePlayerList(playersList: List[PlayerReference]): Unit = {
      resetMap()
      println(playersList)
      println("Aggiorno la mappa")
      playersList.foreach(player => registerUser(player.playerID, player.playerRef))
    }


    override def unregisterUser(userID: String): Unit = socialActorsMap.unregisterUser(userID)

    override def registerUser(userID: String, socialActorRef: ActorRef): Unit = {
      if (!userID.equals(playerID)) {
        socialActorsMap.registerUser(userID, socialActorRef)
      }
    }

    override def resetMap(): Unit = socialActorsMap.resetMap()

    override def setFriendsList(friendList: FriendList): Unit =
      friendList.foreach(friend => this.friendList = friend :: this.friendList)

    override def updateFriendList(friendID: PlayerID): Unit = this.friendList = friendID :: friendList

    override def getAllOnlineFriends: FriendList =
      friendList.filter(friendID =>
        socialActorsMap.getCurrentOnlinePlayerMap.keySet.exists(_.equals(friendID)))

    override def getAllOnlineStrangers: FriendList =
      socialActorsMap.getCurrentOnlinePlayerMap.keySet
        .filter(playerID => !friendList.exists(playerID.equals(_)))
        .toList
  }

}
