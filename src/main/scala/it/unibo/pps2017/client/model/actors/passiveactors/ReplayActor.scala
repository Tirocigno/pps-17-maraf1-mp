
package it.unibo.pps2017.client.model.actors.passiveactors

import akka.actor.{ActorSystem, PoisonPill}
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.passiveactors.ReplayActor._
import it.unibo.pps2017.client.model.actors.passiveactors.ReplayActorStatus._
import it.unibo.pps2017.client.model.actors.playeractor.ClientGameActor
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.core.deck.cards.Seed._
import it.unibo.pps2017.server.model._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object ReplayActor {
  val CARD_PATH: String = "cards/"
  val CARD_FORMAT: String = ".png"
  val START_DELAY: Int = 5000
  val INTERVAL_TIME: Int = 500
  case class SendHeartbeat()

}

class ReplayActor(override val controller: GameController, player: String, game: Game) extends ClientGameActor {

  import system.dispatcher

  var user: String = player
  val system: ActorSystem = akka.actor.ActorSystem("Akka", ConfigFactory.load("redisConf"))
  var gameCounter: ReplayActorStatus = PRE_SET
  var cardsListPlayer = new ListBuffer[String]()
  var playersList = new ListBuffer[String]()
  var briscolaChosen: Seed = _
  var team1Score: Int = 0
  var team2Score: Int = 0
  var currentSet: GameSet = _
  var currentHand: Hand = _
  var currentMove: Move = _

  override def preStart() {
    currentSet = game.turns.head
    system.scheduler.schedule(
      initialDelay = START_DELAY.milliseconds,
      interval = INTERVAL_TIME.milliseconds,
      receiver = self,
      message = SendHeartbeat)
  }

  def receive: PartialFunction[Any, Unit] = {

    case SendHeartbeat => gameCounter match {
      case PRE_SET => computePreSet()
      case START_SET => computeStartSet()
      case TURN_SET => computeTurnSet()
      case MIDDLE_SET => computeMiddleSet()
      case END_SET => computeEndSet()
      case END_GAME => computeEndGame()
    }
  }

  private def computePreSet(): Unit = {
    game.players.foreach(player => {
      playersList += player
    })
    controller.updateGUI(PlayersRef(playersList))
    gameCounter = START_SET
  }

  private def computeStartSet(): Unit = {
    convertBriscolaSeed(currentSet.briscola)
    team1Score = currentSet.team1Score
    team2Score = currentSet.team2Score
    cardsListPlayer clear()
    currentSet.playersHand.foreach(player => {
      if (playersList.head.equals(player._1)) player._2.foreach(card => {
        var actualCard = CARD_PATH + card + CARD_FORMAT
        cardsListPlayer += actualCard
      })
    })

    controller.updateGUI(DistributedCard(cardsListPlayer.toList, playersList.head))
    controller.updateGUI(NotifyBriscolaChosen(briscolaChosen))
    currentHand = currentSet.hands.head
    currentMove = currentHand.moves.head
    gameCounter = TURN_SET
  }

  private def computeTurnSet(): Unit = {
    controller.updateGUI(Turn(currentHand.moves.head.player,
      endPartialTurn = true, isFirstPlayer = false, isReplay = true))
    gameCounter = MIDDLE_SET
  }

  private def computeMiddleSet(): Unit = {
    val actualCard = CARD_PATH + currentMove.card + CARD_FORMAT
    controller.updateGUI(PlayedCard(actualCard, currentMove.player))
    try {
      currentMove = currentHand.moves(currentHand.moves.indexOf(currentMove) + 1)
    } catch {
      case _: Exception => try {
        gameCounter = TURN_SET
        currentHand = currentSet.hands(currentSet.hands.indexOf(currentHand) + 1)
        currentMove = currentHand.moves.head
      } catch {
        case _: Exception => gameCounter = END_SET
      }
    }

  }

  private def computeEndSet(): Unit = {
    try {
      currentSet = game.turns(game.turns.indexOf(currentSet) + 1)
      if (team1Score > team2Score)
        controller.updateGUI(ComputeGameScore(playersList.head, playersList.head, playersList.head,
          team1Score, team2Score, endMatch = false))
      else controller.updateGUI(ComputeGameScore(user, playersList.head, playersList.head,
        team1Score, team2Score, endMatch = false))
      gameCounter = START_SET
    } catch {
      case _: Exception => gameCounter = END_GAME
        if (team1Score > team2Score)
          controller.updateGUI(ComputeGameScore(playersList.head, playersList.head, playersList.head,
            team1Score, team2Score, endMatch = true))
        else controller.updateGUI(ComputeGameScore(user, playersList.head, playersList.head,
          team1Score, team2Score, endMatch = true))
    }
  }

  private def computeEndGame(): Unit = {
    self ! PoisonPill
  }

  override def username: String = user

  private def convertBriscolaSeed(briscola: String): Unit = briscola match {
    case Sword.asString => briscolaChosen = Sword
    case Cup.asString => briscolaChosen = Cup
    case Coin.asString => briscolaChosen = Coin
    case Club.asString => briscolaChosen = Club
  }
}


object ReplayActorStatus {

  sealed trait ReplayActorStatus {
    def asString: String
  }

  /**
    * In PRE_SET state we catch four players in game and send to the Gui.
    */
  case object PRE_SET extends ReplayActorStatus {
    override val asString: String = "pre_set"
  }

  /**
    * In STARTS_SET state we catch briscola of this turn, score of both team and cards of first player.
    */
  case object START_SET extends ReplayActorStatus {
    override val asString: String = "start_set"
  }

  /**
    * In TURN_SET state we catch turn of next player.
    */
  case object TURN_SET extends ReplayActorStatus {
    override val asString: String = "turn_set"
  }

  /**
    * In MIDDLE_SET state we catch every move of actual turn.
    */
  case object MIDDLE_SET extends ReplayActorStatus {
    override val asString: String = "middle_set"
  }

  /**
    * In END_SET state we catch teams' score and reset the state to START_SET.
    * If the match is ended, we set the state to END_GAME.
    */
  case object END_SET extends ReplayActorStatus {
    override val asString: String = "end_set"
  }

  /**
    * In END_GAME we kill actor with PoisonPill.
    */
  case object END_GAME extends ReplayActorStatus {
    override val asString: String = "end_game"
  }

}
