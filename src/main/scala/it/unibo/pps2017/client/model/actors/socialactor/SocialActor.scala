
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.{ActorRef, ActorSystem, Props}
import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.client.model.actors.ModelActor

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
  def apply(system: ActorSystem, socialController: SocialController): ActorRef =
    system.actorOf(Props(new SocialActorImpl(socialController)))

  private class SocialActorImpl(override val controller: SocialController) extends SocialActor {
    override def receive: Receive = ???
  }

}
