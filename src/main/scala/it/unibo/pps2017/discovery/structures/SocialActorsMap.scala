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
