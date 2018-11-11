
package it.unibo.pps2017.discovery.actors

import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, SocialMap}
import it.unibo.pps2017.discovery.actors.RegistryActor.{AddUserToRegisterMessage, HeartBeatMessage, OnlinePlayerListMessage, RemoveUserFromRegisterMessage}
import it.unibo.pps2017.discovery.structures.SocialActorsMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * Registry actor to supply the currentOnlinePlayer list in real time.
  */
class RegistryActor extends Actor {

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  val socialActorsMap: SocialActorsMap = SocialActorsMap()


  override def receive: Receive = {
    case AddUserToRegisterMessage(playerID, actorRef) =>
      socialActorsMap.registerUser(playerID, actorRef)
      notifyListUpdate()
    case RemoveUserFromRegisterMessage(playerID) =>
      socialActorsMap.unregisterUser(playerID)
      notifyListUpdate()
    case HeartBeatMessage(_) => {
      context.system.scheduler.scheduleOnce(RegistryActor.BEAT_DELAY second) {
        mediator ! Publish(RegistryActor.SOCIAL_CHANNEL, HeartBeatMessage(self))
      }
    }
  }

  /**
    * Publish the current players list on the channel.
    */
  private def notifyListUpdate(): Unit = {
    mediator ! Publish(RegistryActor.SOCIAL_CHANNEL, OnlinePlayerListMessage(socialActorsMap.getCurrentOnlinePlayerMap))
  }
}

object RegistryActor {

  /**
    * This delay is absolutely necessary for the correct operation of the publish subscribe mediator.
    * Without it, a message sent to a registered subscribe, even if he has received the subscription ack will be lost.
    */
  val BEAT_DELAY: Int = 5
  /**
    * ID of the channel on which the messages will be sent.
    */
  val SOCIAL_CHANNEL = "ONLINE_PLAYERS_REGISTRY"

  /**
    * Tell the actor to add a new player.
    *
    * @param userID the id of the user to add.
    * @param sender the actorRef of the sender.
    */
  case class AddUserToRegisterMessage(userID: PlayerID, sender: ActorRef)

  /**
    * Remove a user from the remote list.
    *
    * @param userID the id of the user to remove
    */
  case class RemoveUserFromRegisterMessage(userID: PlayerID)

  /**
    * Return the current online players list.
    *
    * @param map the map of current online players
    */
  case class OnlinePlayerListMessage(map: SocialMap)

  /**
    * Trigger an heartbeat on the channel.
    *
    * @param registryActorRef the actor ref of the registry, if none, this message is used to trigger a new heartbeat
    */
  case class HeartBeatMessage(registryActorRef: ActorRef)

}
