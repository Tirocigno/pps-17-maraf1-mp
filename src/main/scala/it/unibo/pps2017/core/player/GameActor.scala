package it.unibo.pps2017.core.player

import java.util.TimerTask

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.game.MatchManager.{FOUR_OF_COIN, RANDOM_TEAM}
import it.unibo.pps2017.core.game._

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import it.unibo.pps2017.core.player.GameActor._
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp

import scala.collection.mutable


class GameActor extends Actor with Match with ActorLogging {

  var currentBriscola: Option[Seed] = None
  var currentSuit: Option[Seed] = None
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()
  var firstHand: Boolean = true
  val cardsOnTable: mutable.ListBuffer[(Card, Player)] = mutable.ListBuffer()
  var nextHandStarter: Option[Player] = None
  var setEnd: Boolean = false
  var gameEnd: Boolean = false
  var team1: Team = _
  var team2: Team = _
  val actors : ListBuffer[ActorRef] = ListBuffer[ActorRef]()
  var cardsInHand :  collection.Map[ActorRef, ListBuffer[Card]] =  Map[ActorRef, ListBuffer[Card]]()
  var matchh = MatchManager()
  var cardsInTable : ListBuffer[Card] = ListBuffer[Card]()
  var mediator : ActorRef  = _
  val cluster = Cluster(context.system)
  var task : TimerTask = _
  var cardPlayed : Boolean = false

  override def preStart(): Unit = {
    mediator = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(TOPIC_NAME, self)
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case SubscribeAck(Subscribe(TOPIC_NAME, None, _)) ⇒
      log.info("subscribing")

    case RegisterPlayer(player) => {
       actors.length match {
        case x if x < TOT_PLAYERS - 1 => {
          addPlayer(player,"NOME_TEAM")
          actors += player.playerRef
          onFullTable()
        }
        case TOT_PLAYERS => {
          println("Already 4 players")
        }
        case _ => {
           addPlayer(player,"NOME_TEAM")
          actors += player.playerRef
        }
      }
    }

    case BriscolaChosen(seed) => {
      setBriscola(seed)
      mediator ! Publish(TOPIC_NAME, NotifyBriscolaChosen(seed,nextHandStarter.get))
      mediator ! Publish(TOPIC_NAME,Turn(nextHandStarter.get.playerRef,false,true))
      startTimer()
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

  def startTimer(): Unit = {
    var tmp = 0
    val timer = new java.util.Timer()
    task = new java.util.TimerTask {
      def run() = {
        tmp = tmp + 1
        cardPlayed match {
          case true => {
            cardPlayed = false
            endTask()
          }
          case false => {
            if(tmp == TURN_TIME_SEC) controller.getRandCard()
          }
        }
      }
    }
    timer.schedule(task, TIME_PERIOD, TIME_PERIOD)
  }

  def endTask(): Unit ={
    task.cancel()
  }

  override def addPlayer(newPlayer: Player, team: String = RANDOM_TEAM): Unit = {
    try {
      addPlayerToTeam(newPlayer, team)
    } catch {
      case teamNotFoundException: TeamNotFoundException => println("EXCEPTION /" + teamNotFoundException.message)
      case fullTeamException: FullTeamException => println("EXCEPTION /" + fullTeamException.message)
    }
  }

  @throws(classOf[FullTeamException])
  @throws(classOf[TeamNotFoundException])
  private def addPlayerToTeam(player: Player, teamName: String = RANDOM_TEAM): Unit = {
    if (teamName.equals(RANDOM_TEAM)) {
      try {
        team1.addPlayer(player)
      } catch {
        case _: FullTeamException =>
          try {
            team2.addPlayer(player)
          } catch {
            case _: FullTeamException => throw FullTeamException()
          }
      }
    } else {
      getTeamForName(teamName).addPlayer(player)
    }
  }

  @throws(classOf[TeamNotFoundException])
  private def getTeamForName(teamName: String): Team = {
    if (team1.name.equalsIgnoreCase(teamName)) return team1

    if (team2.name.equalsIgnoreCase(teamName)) return team2

    throw TeamNotFoundException("Team '" + teamName + "' not found in this match")
  }

  private def onFullTable(): Unit = {
    gameCycle = GameCycle(team1, team2)
    startGame()
  }

  override def startGame(): Unit = {
    firstHand = true
    startSet()
  }

  private def startSet(): Unit = {
    playSet()
  }


  override def playSet(): Unit = {
    prepareSet()

    nextHandStarter match {
      case Some(player) => setBriscola(player.onSetBriscola())
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
  }


  private def prepareSet(): Unit = {
    setEnd = false
    deck.shuffle()
    var i: Int = 0
    deck.distribute().foreach(hand => {
      actors.foreach(player => {
        mediator ! Publish(TOPIC_NAME, DistributedCard(hand,player))
      })
      if (firstHand && isFirstPlayer(hand)) {
          nextHandStarter = Some(getPlayers(i))
          firstHand = false
      }
      i += 1
    })
    mediator ! Publish(TOPIC_NAME,SelectBriscola(nextHandStarter.get.playerRef))

  }

  def getPlayers: Seq[Player] = {
    if (gameCycle != null) return gameCycle.queue

    team1.getMembers ++ team2.getMembers
  }

  private def isFirstPlayer(hand: Set[Card]): Boolean = hand.contains(FOUR_OF_COIN)

  override def setBriscola(seed: Seed.Seed): Unit = {
    currentBriscola = Option(seed)
    startHand()
  }

  override def isSetEnd: Option[(Int, Int, Boolean)] = ???

  override def forcePlay(player: Player): Card = ???

  override def isCardOk(card: Card): Boolean = ???

  private def startHand(): Unit = {
    nextHandStarter match {
      case Some(player) => gameCycle.setFirst(player)
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
    gameCycle.getCurrent.onMyTurn()
  }
}

object GameActor {
  val TOT_PLAYERS: Int = 4
  val TOPIC_NAME = "CHANNEL"
  val TURN_TIME_SEC: Int = 10
  val TIME_PERIOD: Long = 1000L

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

