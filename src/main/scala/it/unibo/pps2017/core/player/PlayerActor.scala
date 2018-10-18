package it.unibo.pps2017.core.player

import akka.actor.Actor

class PlayerActor(val username:String) extends Actor{
   def receive = {
     case _ =>
   }
}

object PlayerActor{

  def apply(name:String): PlayerActor =  PlayerActor(name)
}
