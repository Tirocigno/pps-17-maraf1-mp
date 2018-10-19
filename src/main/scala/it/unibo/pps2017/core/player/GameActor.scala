package it.unibo.pps2017.core.player

import java.util.TimerTask

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.core.deck.cards.Seed.{Coin, Seed}
import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.game._
import it.unibo.pps2017.core.player.GameActor._

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import scala.util.Random


class GameActor(val topicName: String, val team1: BaseTeam[String], val team2: BaseTeam[String], onGameEnd:()=>Unit) extends Actor with Match with ActorLogging {

  type PlayerName = String
  var currentBriscola: Option[Seed] = None
  var currentSuit: Option[Seed] = None
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()
  var firstHand: Boolean = true
  val cardsOnTable: mutable.ListBuffer[(Card, PlayerName)] = mutable.ListBuffer()
  var nextHandStarter: Option[PlayerName] = None
  var setEnd: Boolean = false
  var gameEnd: Boolean = false
  val actors: ListBuffer[PlayerName] = ListBuffer[PlayerName]()
  var cardsInHand: collection.Map[PlayerName, ArrayBuffer[Card]] = Map[PlayerName, ArrayBuffer[Card]]()
  var mediator : ActorRef  = _
  val cluster = Cluster(context.system)
  var task : TimerTask = _
  var cardPlayed : Boolean = false
  var numAck: Int = 0

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
    mediator = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(topicName, self)

    team1.getMembers.foreach(player =>{
      actors += player
    })

    team2.getMembers.foreach(player =>{
      actors += player
    })

    mediator ! Publish(topicName,PlayersRef(actors))

  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case PlayersRefAck =>
      numAck = numAck +1
      if(numAck==TOT_PLAYERS) {
        onFullTable()
      }
      numAck = 0


    case BriscolaChosen(seed) =>
      setBriscola(seed)
      mediator ! Publish(topicName, NotifyBriscolaChosen(seed))


    case BriscolaAck =>
      numAck = numAck + 1
      if(numAck==TOT_PLAYERS){
        mediator ! Publish(topicName, Turn(nextHandStarter.get, setEnd, true))
        startTimer()
        numAck = 0
      }


    case ClickedCard(index,player) =>
      isCardOk(cardsInHand(player)(index),player)


    case CardPlayedAck =>
      numAck = numAck + 1
      if(numAck==TOT_PLAYERS) {
        if(gameCycle.isLast){
          onHandEnd(defineTaker(cardsOnTable))
        }
        mediator ! Publish(topicName, Turn(gameCycle.next(),setEnd, gameCycle.isFirst))
        numAck = 0
      }


    case ClickedCommand(command, player) =>
      mediator ! Publish(topicName, NotifyCommandChosen(command,player))
  }


  private def startTimer(): Unit = {
    var tmp = 0
    val timer = new java.util.Timer()
    task = new java.util.TimerTask {
      def run(): Unit = {
        tmp = tmp + 1
            if(tmp == TURN_TIME_SEC) {
              val randCard: Card = forcePlay(nextHandStarter.get)
              mediator ! Publish(topicName,ForcedCardPlayed(cardToPath(randCard),nextHandStarter.get))
            }
      }
    }
    timer.schedule(task, TIME_PERIOD, TIME_PERIOD)
  }

  private def endTask(): Unit ={
    task.cancel()
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
        mediator ! Publish(topicName, DistributedCard(allCardsToPath(hand),player))
      })
      if (firstHand && isFirstPlayer(hand)) {
          nextHandStarter = Some(getPlayers(i))
          firstHand = false
      }
      i += 1
    })
    mediator ! Publish(topicName,SelectBriscola(nextHandStarter.get))

  }

  private def cardToPath(card: Card): String =  IMG_PATH + card.cardValue + card.cardSeed + PNG_FILE

  private def allCardsToPath(cards: Set[Card]): List[String] = {
    var allCardsPath : ListBuffer[String] = ListBuffer[String]()
    cards.foreach(card =>
      allCardsPath += cardToPath(card)
    )
    allCardsPath.toList
  }

  def getPlayers: Seq[PlayerName] = {
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

  override def forcePlay(player: PlayerName): Card = {
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

  override def isCardOk(card: Card, player: PlayerName): Unit = currentSuit match {
    case Some(seed) =>
      val playerHand: Seq[Card] = cardsInHand.get(player).get.toStream

      if (seed == card.cardSeed) {
        onCardPlayed(card, player)
      }
      else if (!playerHand.exists(_.cardSeed == seed)) {
        onCardPlayed(card, player)
      }
      else {
        mediator ! Publish(topicName, CardOk(false, player))
      }
    case None =>
      onCardPlayed(card, player)
  }

  private def onCardPlayed(card: Card, player: PlayerName): Unit = {
    mediator ! Publish(topicName, CardOk(true, player))

    if (gameCycle.isFirst) onFirstCardOfHand(card)

    cardsOnTable += ((card, gameCycle.getCurrent))
    cardPlayed = true
    mediator ! Publish(topicName,PlayedCard(cardToPath(card), player))
    cardsInHand.get(player).get -= card
  }

  private def onFirstCardOfHand(card: Card): Unit = {
    val currentHand: Set[Card] = cardsInHand.get(gameCycle.getCurrent).get.toSet
    if (currentHand.size == MAX_HAND_CARDS && currentBriscola.get == card.cardSeed && card.cardValue == ACE_VALUE) {
      checkMarafona(currentHand, gameCycle.getCurrent)
    }
    currentSuit = Option(card.cardSeed)
  }

  private def onHandEnd(lastTaker: PlayerName): Unit = {
    setEnd = true
    deck.registerTurnPlayedCards(cardsOnTable.map(_._1), getTeamOfPlayer(lastTaker))
    nextHandStarter = Some(lastTaker)
    currentSuit = None
    cardsOnTable.clear()

    nextHandStarter match {
      case Some(player) => if (cardsInHand.get(player).get.isEmpty) onSetEnd()
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
      case _ =>
        if(team1.getScore > team2.getScore){
          mediator ! Publish(topicName, PartialGameScore(team1.firstMember.get, team1.secondMember.get, team1.getScore, team2.getScore))
        }
        else{
          mediator ! Publish(topicName, PartialGameScore(team2.firstMember.get, team2.secondMember.get, team1.getScore, team2.getScore))
        }

    }
  }

  private def getTeamOfPlayer(player: PlayerName): BaseTeam[PlayerName] = getTeamIndexOfPlayer(player) match {
    case 0 => team1
    case 1 => team2
  }

  private def getTeamIndexOfPlayer(player: PlayerName): Int = {
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

  private def getGameWinner: Option[BaseTeam[PlayerName]] = {
    if (team1.getScore >= MAX_SCORE && team1.getScore > team2.getScore) return Some(team1)

    if (team2.getScore >= MAX_SCORE && team2.getScore > team1.getScore) return Some(team2)

    None
  }

  private def notifyWinner(team: BaseTeam[PlayerName]): Unit = {
    gameEnd = true
    mediator ! Publish(topicName, FinalGameScore(team.firstMember.get,team.secondMember.get,team1.getScore, team2.getScore))
    onGameEnd()
  }

  private def checkMarafona(hand: Set[Card], player: PlayerName): Unit = {
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

  private def defineTaker(hand: mutable.ListBuffer[(Card, PlayerName)]): PlayerName = {
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

  def apply(topicName: String, team1: BaseTeam[String], team2: BaseTeam[String], onGameEnd:()=>Unit): GameActor = new GameActor(topicName, team1, team2, onGameEnd)

}

