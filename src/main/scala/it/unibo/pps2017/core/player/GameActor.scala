package it.unibo.pps2017.core.player

import java.util.TimerTask

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.deck.cards.Seed.{Coin, Seed}
import it.unibo.pps2017.core.game.MatchManager.{FOUR_OF_COIN, RANDOM_TEAM}
import it.unibo.pps2017.core.game._

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import it.unibo.pps2017.core.player.GameActor._
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp

import scala.collection.mutable
import scala.util.Random


class GameActor extends Actor with Match with ActorLogging {

  var currentBriscola: Option[Seed] = None
  var currentSuit: Option[Seed] = None
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()
  var firstHand: Boolean = true
  val cardsOnTable: mutable.ListBuffer[(Card, PlayerActor)] = mutable.ListBuffer()
  var nextHandStarter: Option[PlayerActor] = None
  var setEnd: Boolean = false
  var gameEnd: Boolean = false
  var team1: Team = _
  var team2: Team = _
  val actors : ListBuffer[PlayerActor] = ListBuffer[PlayerActor]()
  var cardsInHand :  collection.Map[PlayerActor, ArrayBuffer[Card]] =  Map[PlayerActor, ArrayBuffer[Card]]()
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
          addPlayer(player,RANDOM_TEAM)
          actors += player
          onFullTable()
        }
        case TOT_PLAYERS => {
          println("Already 4 players")
        }
        case _ => {
          addPlayer(player,RANDOM_TEAM)
          actors += player
        }
      }
    }

    case BriscolaChosen(seed) => {
      setBriscola(seed)
      mediator ! Publish(TOPIC_NAME, NotifyBriscolaChosen(seed))
      val setEnd = gameEnd
      mediator ! Publish(TOPIC_NAME,Turn(nextHandStarter.get,setEnd,true))
      startTimer()
    }

    case ClickedCard(index,player) => {
      isCardOk(cardsInHand.get(player).get(index),player)
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
    mediator ! Publish(TOPIC_NAME,PlayersRef(actors))
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

  override def isSetEnd: Option[(Int, Int, Boolean)] = {
    if (setEnd) {
      /*if(team1.getScore > team2.getScore){
        mediator ! Publish(TOPIC_NAME, PartialGameScore(team1.firstMember.get, team1.secondMember.get, team1.getScore, team2.getScore))
      }
      else{
        mediator ! Publish(TOPIC_NAME, PartialGameScore(team2.firstMember.get, team2.secondMember.get, team1.getScore, team2.getScore))
      }*/
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

  override def isCardOk(card: Card, player: PlayerActor): Boolean = currentSuit match {
    case Some(seed) =>
      if (seed == card.cardSeed) {
        onCardPlayed(card, player)
        return true
      }

      val playerHand: Seq[Card] = cardsInHand.get(player).get.toStream

      if (!playerHand.exists(_.cardSeed == seed)) {
        onCardPlayed(card, player)
        return true
      }
      mediator ! Publish(TOPIC_NAME,CardOk(false))
      false
    case None =>
      onCardPlayed(card, player)
      true
  }

  private def onCardPlayed(card: Card, player: PlayerActor): Unit = {
    if (gameCycle.isFirst) onFirstCardOfHand(card)

    cardsOnTable += ((card, gameCycle.getCurrent))
    cardPlayed = true
    mediator ! Publish(TOPIC_NAME,CardOk(true))
    val cardPath: String = IMG_PATH + card.cardValue + card.cardSeed + PNG_FILE
    mediator ! Publish(TOPIC_NAME,PlayedCard(cardPath, player))
    cardsInHand.get(player).get -= card

    if (!gameCycle.isLast) {
      mediator ! Publish(TOPIC_NAME, Turn(gameCycle.next(),gameEnd, gameCycle.isFirst))
    } else {
     //onHandEnd(cardsOnTable)
    }
  }

  private def onFirstCardOfHand(card: Card): Unit = {
    val currentHand: Set[Card] = cardsInHand.get(gameCycle.getCurrent).get.toSet
    if (currentHand.size == MAX_HAND_CARDS && currentBriscola.get == card.cardSeed && card.cardValue == ACE_VALUE) {
      checkMarafona(currentHand, gameCycle.getCurrent)
    }
    currentSuit = Option(card.cardSeed)
  }

  private def onHandEnd(lastTaker: PlayerActor): Unit = {
    deck.registerTurnPlayedCards(cardsOnTable.map(_._1), getTeamOfPlayer(lastTaker))
    nextHandStarter = Some(lastTaker)
    mediator ! Publish(TOPIC_NAME, Turn(nextHandStarter.get, gameEnd, gameCycle.isFirst))
    currentSuit = None
    cardsOnTable.clear()


    nextHandStarter match {
      case Some(player) => if (cardsInHand.get(player).get.isEmpty) onSetEnd()
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
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
}

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

