
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.client.model.actors.ModelActor
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference

/**
  * The socialActor will be responsable of all the function in which real time
  * connection is necessary, such as sending and receiving friendship and
  * challenge requests.
  */
//TODO IMPLEMENT THIS ACTOR.
trait SocialActor extends ModelActor {
  override val controller: SocialController
}

object SocialActor {
  def apply(system: ActorSystem, socialController: SocialController, username: String): ActorRef =
    system.actorOf(Props(new SocialActorImpl(socialController, username)))

  private class SocialActorImpl(override val controller: SocialController, override val username: String)
    extends SocialActor {
    val currentContext = PlayerReference(username, self)
    override def receive: Receive = ???
  }

}
