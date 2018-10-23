package it.unibo.pps2017.discovery.structures

import akka.actor.ActorRef

/**
  * This structure will handle a list of actor refs which correspond to the online players logged in the system.
  */
trait SocialActorsMap {

  /**
    * Add a new user on the online player list.
    *
    * @param userID         the id of the user.
    * @param socialActorRef the user Social Actor Ref.
    */
  def registerUser(userID: String, socialActorRef: ActorRef)

  /**
    * Remove a user from the online players list
    *
    * @param userID the id of the player to remove.
    */
  def unregisterUser(userID: String)

  /**
    * Return the map of current online players.
    */
  def getCurrentOnlinePlayerMap: Map[String, ActorRef]

}

object SocialActorsMap {

  def apply(): SocialActorsMap = new SocialActorsMapImpl()

  private class SocialActorsMapImpl extends SocialActorsMap {

    var actorsMap: scala.collection.mutable.Map[String, ActorRef] =
      scala.collection.mutable.Map[String, ActorRef]()

    override def registerUser(userID: String, socialActorRef: ActorRef): Unit =
      actorsMap += (userID -> socialActorRef)

    override def unregisterUser(userID: String): Unit = actorsMap -= userID

    override def getCurrentOnlinePlayerMap: Map[String, ActorRef] = actorsMap.toMap
  }

}
