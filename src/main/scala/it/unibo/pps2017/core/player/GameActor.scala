package it.unibo.pps2017.core.player

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.game.MatchManager
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class GameActor extends Actor with ActorLogging{

  val actors : ListBuffer[ActorRef] = ListBuffer[ActorRef]()
  var cardsInHand :  collection.Map[ActorRef, ListBuffer[Card]] =  Map[ActorRef, ListBuffer[Card]]()
  var matchh = MatchManager()
  var cardsInTable : ListBuffer[Card] = ListBuffer[Card]()

  def receive = {
    case ReadyToPlay => {

        actors.filter(a => !a.equals(this)).foreach( a=>{
          //shuffle + distribute deck + getHand()
        })
    }
    case DistributedCard(cards) => {
      cardsInHand += ( self -> cards)
    }
    case ClickedCard(index) => {
     var card: Card = null
      //check cardOk? then
      cardsInHand.filter(a => a.equals(self)).get(self).foreach(c=> card = c.apply(index))
      cardsInTable += card
    }

    case ClickedCommand(command) => {

    }
  }

}

object GameActor {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """)
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load("factorial"))

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[GameActor], name = "GameActor")
  }
}

