
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, PlayerReference, SocialMap}
import it.unibo.pps2017.discovery.structures.SocialActorsMap

/**
  * This trait extends the social actor map defined on the registry actor, adapting it to social functions.
  */
trait SocialPlayersMap extends SocialActorsMap {

  /**
    * Return the actorRef corresponding to the friend name taken as input.
    *
    * @param friendID the friend's username.
    * @return the current actorRef of the friend.
    */
  def getPlayerID(friendID: PlayerID): ActorRef

  /**
    * Set the online players list.
    *
    * @param playersList the list of the id's of all online players.
    */
  def setOnlinePlayerList(playersList: List[PlayerReference]): Unit

  /**
    * Set player's friend list.
    *
    * @param friendList a list containing all player's friend usernames.
    */
  def setFriendsList(friendList: FriendList): Unit

  /**
    * Add a new friend to the friend list.
    *
    * @param friendID the new friend's username.
    */
  def updateFriendList(friendID: PlayerID): Unit

  /**
    * Get a list containing all online players who are not friend with the player.
    *
    * @return a list containing all strangers usernames.
    */
  def getAllOnlineStrangers: FriendList

  /**
    * Get the list of current online friends.
    *
    * @return a list containing all online friends usernames.
    */
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
