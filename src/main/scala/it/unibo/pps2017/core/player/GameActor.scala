package it.unibo.pps2017.core.player

import java.util.TimerTask

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.deck.cards.Seed.{Coin, Seed}
import it.unibo.pps2017.core.game._

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import it.unibo.pps2017.core.player.GameActor._
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import DistributedPubSubMediator.{Publish, Subscribe}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp

import scala.collection.mutable
import scala.util.Random


class GameActor(val topicName: String, val team1: Team, val team2: Team, onGameEnd:()=>Unit) extends Actor with Match with ActorLogging {
  type ActorName = String

  var currentBriscola: Option[Seed] = None
  var currentSuit: Option[Seed] = None
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()
  var firstHand: Boolean = true
  val cardsOnTable: mutable.ListBuffer[(Card, PlayerActor)] = mutable.ListBuffer()
  var nextHandStarter: Option[PlayerActor] = None
  var setEnd: Boolean = false
  var gameEnd: Boolean = false
  val actors : ListBuffer[PlayerActor] = ListBuffer[PlayerActor]()
  var cardsInHand :  collection.Map[PlayerActor, ArrayBuffer[Card]] =  Map[PlayerActor, ArrayBuffer[Card]]()
  var cardsInTable : ListBuffer[Card] = ListBuffer[Card]()
  var mediator : ActorRef  = _
  val cluster = Cluster(context.system)
  var task : TimerTask = _
  var cardPlayed : Boolean = false
  var numAck: Int = 0

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
    mediator = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(TOPIC_NAME, self)
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

   /* case RegisterPlayer(team1, team2) => {

      team1.getMembers.foreach(player =>{
        addPlayer(player,RANDOM_TEAM)
        //actors += player.getUsername()
      })

      team2.getMembers.foreach(player =>{
        addPlayer(player,RANDOM_TEAM)
        //actors += player.getUsername()
      })

      onFullTable()
    }*/

    case PlayersRefAck =>{
      numAck = numAck +1
      if(numAck==TOT_PLAYERS) {
        onFullTable()
      }
      numAck = 0
    }

    case BriscolaChosen(seed) => {
      setBriscola(seed)
      mediator ! Publish(TOPIC_NAME, NotifyBriscolaChosen(seed))
    }

    case BriscolaAck =>{
      numAck = numAck + 1
      if(numAck==TOT_PLAYERS){
        mediator ! Publish(TOPIC_NAME, Turn(nextHandStarter.get, setEnd, true))
        startTimer()
        numAck = 0
      }
    }

    case ClickedCard(index,player) => {
      isCardOk(cardsInHand.get(player).get(index),player)
    }

    case PlayedCardAck => {
      numAck = numAck + 1
      if(numAck==TOT_PLAYERS) {
        if(gameCycle.isLast){
          onHandEnd(defineTaker(cardsOnTable))
        }
        mediator ! Publish(TOPIC_NAME, Turn(gameCycle.next(),setEnd, gameCycle.isFirst))
        numAck = 0
      }
    }

    case ClickedCommand(command, player) => {
      mediator ! Publish(TOPIC_NAME, NotifyCommandChosen(command,player))
    }
  }

  private def startTimer(): Unit = {
    var tmp = 0
    val timer = new java.util.Timer()
    task = new java.util.TimerTask {
      def run() = {
        tmp = tmp + 1
            if(tmp == TURN_TIME_SEC) {
              val randCard: Card = forcePlay(nextHandStarter.get)
              val cardPath: String = IMG_PATH + randCard.cardValue + randCard.cardSeed + PNG_FILE

              mediator ! Publish(TOPIC_NAME,ForcedCardPlayed(cardPath,nextHandStarter.get))
            }
      }
    }
    timer.schedule(task, TIME_PERIOD, TIME_PERIOD)
  }

  private def endTask(): Unit ={
    task.cancel()
  }

  override def addPlayer(newPlayer: PlayerActor, team: String = RANDOM_TEAM): Unit = {
    try {
      addPlayerToTeam(newPlayer, team)
    } catch {
      case teamNotFoundException: TeamNotFoundException => println("EXCEPTION /" + teamNotFoundException.message)
      case fullTeamException: FullTeamException => println("EXCEPTION /" + fullTeamException.message)
    }
  }

  @throws(classOf[FullTeamException])
  @throws(classOf[TeamNotFoundException])
  private def addPlayerToTeam(player: PlayerActor, teamName: String = RANDOM_TEAM): Unit = {
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
  }


  private def prepareSet(): Unit = {
    var actorReferences: List[ActorName] = List[ActorName]()
    actors.foreach(p => {
      //actorReferences += p.getUsername()
    })
    //mediator ! Publish(TOPIC_NAME,PlayersRef(actorReferences))
    setEnd = false
    deck.shuffle()
    var i: Int = 0
    deck.distribute().foreach(hand => {
      actors.foreach(player => {
        mediator ! Publish(TOPIC_NAME, DistributedCard(cardsToPath(hand),player))
      })
      if (firstHand && isFirstPlayer(hand)) {
          nextHandStarter = Some(getPlayers(i))
          firstHand = false
      }
      i += 1
    })
    mediator ! Publish(TOPIC_NAME,SelectBriscola(nextHandStarter.get))

  }

  private def cardsToPath(cards: Set[Card]): List[String] = {
    var allCardsPath : ListBuffer[String] = ListBuffer[String]()
    cards.foreach(card =>
      allCardsPath += IMG_PATH + card.cardValue + card.cardSeed + PNG_FILE
    )
    allCardsPath.toList
  }

  def getPlayers: Seq[PlayerActor] = {
    if (gameCycle != null) return gameCycle.queue

    team1.getMembers ++ team2.getMembers
  }

  private def isFirstPlayer(hand: Set[Card]): Boolean = hand.contains(FOUR_OF_COIN)

  override def setBriscola(seed: Seed.Seed): Unit = {
    currentBriscola = Option(seed)
    startHand()
  }

//da cancellare?
  override def isSetEnd: Option[(Int, Int, Boolean)] = {
    if (setEnd) {
      Some((team1.getScore, team2.getScore, gameEnd))
    }
    None
  }

  override def forcePlay(player: PlayerActor): Card = {
    currentSuit match {
      case Some(seed) =>
        val rightCards: Seq[Card] = cardsInHand.get(player).get.filter(_.cardSeed == seed).toList
        cardPlayed = true
        endTask()
        rightCards(Random.nextInt(rightCards.size))
      case None =>
        val playerHand: Seq[Card] = cardsInHand.get(player).get.toList
        cardPlayed = true
        endTask()
        playerHand(Random.nextInt(playerHand.size))
    }
  }

  override def isCardOk(card: Card, player: PlayerActor): Unit = currentSuit match {
    case Some(seed) =>
      val playerHand: Seq[Card] = cardsInHand.get(player).get.toStream

      if (seed == card.cardSeed) {
        onCardPlayed(card, player)
      }
      else if (!playerHand.exists(_.cardSeed == seed)) {
        onCardPlayed(card, player)
      }
      else {
        mediator ! Publish(TOPIC_NAME, CardOk(false, player))
      }
    case None =>
      onCardPlayed(card, player)
  }

  private def onCardPlayed(card: Card, player: PlayerActor): Unit = {
    mediator ! Publish(TOPIC_NAME, CardOk(true, player))

    if (gameCycle.isFirst) onFirstCardOfHand(card)

    cardsOnTable += ((card, gameCycle.getCurrent))
    cardPlayed = true
    val cardPath: String = IMG_PATH + card.cardValue + card.cardSeed + PNG_FILE
    mediator ! Publish(TOPIC_NAME,PlayedCard(cardPath, player))
    cardsInHand.get(player).get -= card
  }

  private def onFirstCardOfHand(card: Card): Unit = {
    val currentHand: Set[Card] = cardsInHand.get(gameCycle.getCurrent).get.toSet
    if (currentHand.size == MAX_HAND_CARDS && currentBriscola.get == card.cardSeed && card.cardValue == ACE_VALUE) {
      checkMarafona(currentHand, gameCycle.getCurrent)
    }
    currentSuit = Option(card.cardSeed)
  }

  private def onHandEnd(lastTaker: PlayerActor): Unit = {
    setEnd = true
    deck.registerTurnPlayedCards(cardsOnTable.map(_._1), getTeamOfPlayer(lastTaker))
    nextHandStarter = Some(lastTaker)
    currentSuit = None
    cardsOnTable.clear()

    nextHandStarter match {
      case Some(player) => if (cardsInHand.get(player).get.isEmpty) onSetEnd()
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
      case _ => {
        if(team1.getScore > team2.getScore){
          mediator ! Publish(TOPIC_NAME, PartialGameScore(team1.firstMember.get, team1.secondMember.get, team1.getScore, team2.getScore))
        }
        else{
          mediator ! Publish(TOPIC_NAME, PartialGameScore(team2.firstMember.get, team2.secondMember.get, team1.getScore, team2.getScore))
        }
      }
    }
  }

  private def getTeamOfPlayer(player: PlayerActor): Team = getTeamIndexOfPlayer(player) match {
    case 0 => team1
    case 1 => team2
  }

  private def getTeamIndexOfPlayer(player: PlayerActor): Int = {
    if (team1.getMembers.contains(player)) return 0
    1
  }

  private def onSetEnd(): Unit = {
    setEnd = true
    currentBriscola = None
    nextHandStarter = None

    val setScore = deck.computeSetScore()
    team1.addPoints(setScore._1)
    team2.addPoints(setScore._2)

    getGameWinner match {
      case Some(team) => notifyWinner(team)
      case None => startSet()
    }
  }

  private def getGameWinner: Option[Team] = {
    if (team1.getScore >= MAX_SCORE && team1.getScore > team2.getScore) return Some(team1)

    if (team2.getScore >= MAX_SCORE && team2.getScore > team1.getScore) return Some(team2)

    None
  }

  def notifyWinner(team: Team): Unit = {
    gameEnd = true
    mediator ! Publish(TOPIC_NAME, FinalGameScore(team.firstMember.get,team.secondMember.get,team1.getScore, team2.getScore))
    onGameEnd()
  }

  private def checkMarafona(hand: Set[Card], player: PlayerActor): Unit = {
    if (hand.filter(searchAce => searchAce.cardSeed == currentSuit.get)
      .count(c => c.cardValue == ACE_VALUE || c.cardValue == TWO_VALUE || c.cardValue == THREE_VALUE) == REQUIRED_NUMBERS_OF_CARDS_FOR_MARAFFA) {
      deck.registerMarafona(getTeamOfPlayer(player))
    }

    None
  }

  private def startHand(): Unit = {
    nextHandStarter match {
      case Some(player) => gameCycle.setFirst(player)
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
  }

  implicit def defineTaker(hand: mutable.ListBuffer[(Card, PlayerActor)]): PlayerActor = {
    var max: Card = hand.head._1

    hand foreach (tuple => {
      val card = tuple._1
      if (card.cardSeed == currentBriscola.get) {
        if (max.cardSeed != currentBriscola.get) {
          max = card
        } else if (max < card) {
          max = card
        }
      } else if (max.cardSeed != currentBriscola.get && card.cardSeed == currentSuit.get && max < card) {
        max = card
      }
    })

    hand.filter(_._1 == max).head._2
  }

}


final case class FullTeamException(message: String = "The team has reached the maximum number of players",
                                   private val cause: Throwable = None.orNull) extends Exception(message, cause)

final case class TeamNotFoundException(message: String = "Team not found in this match",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)

object GameActor {
  val TOT_PLAYERS: Int = 4
  val TOPIC_NAME = "CHANNEL"
  val TURN_TIME_SEC: Int = 10
  val TIME_PERIOD: Long = 1000L

  val RANDOM_TEAM: String = "RANDOM_TEAM"
  val TEAM_MEMBERS_LIMIT: Int = 2
  val MAX_PLAYER_NUMBER: Int = 4
  val MAX_HAND_CARDS: Int = 10
  val FOUR_OF_COIN: Card = CardImpl(Coin, 4)
  val MAX_SCORE: Int = 41
  val ACE_VALUE: Int = 1
  val TWO_VALUE: Int = 2
  val THREE_VALUE: Int = 3
  val REQUIRED_NUMBERS_OF_CARDS_FOR_MARAFFA: Int = 3
  val EXTRA_POINTS_FOR_MARAFFA: Int = 3
  val IMG_PATH = "src/main/java/it/unibo/pps2017/core/gui/cards/"
  val PNG_FILE = ".png"

  def apply(topicName: String, team1: Team, team2: Team, func:()=>Unit): GameActor = new GameActor(topicName, team1, team2,func)

 /* def main(args: Array[String]): Unit = {
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
  }*/
}

