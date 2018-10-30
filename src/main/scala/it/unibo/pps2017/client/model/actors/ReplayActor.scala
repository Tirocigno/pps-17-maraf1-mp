
package it.unibo.pps2017.client.model.actors

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ReplayActor.SendHeartbeat
import it.unibo.pps2017.client.model.actors.ReplayActorStatus._
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.core.deck.cards.Seed._
import it.unibo.pps2017.server.model._
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object ReplayActor {

  case class SendHeartbeat()

}

class ReplayActor(override val controller: GameController, username: String, game: Game) extends ClientGameActor {

  import system.dispatcher

  var user: String = username
  val system = ActorSystem("ScheduledActors")
  var gameCounter: ReplayActorStatus = PRE_SET
  var cardsListPlayer = new ListBuffer[String]()
  var playersList = new ListBuffer[String]()
  var briscolaChosen: Seed = _
  var team1Score = 0
  var team2Score = 0
  val FIRST_CARD: Int = 0
  val LAST_CARD: Int = 10
  val CARD_PATH: String = "cards/"
  val CARD_FORMAT: String = ".png"
  var currentSet: GameSet = _
  var currentHand: Hand = _
  var currentMove: Move = _


  override def preStart() {

    currentSet = game.turns.head
    system.scheduler.schedule(
      initialDelay = 0.milliseconds,
      interval = 3000.milliseconds,
      receiver = self,
      message = SendHeartbeat)
  }


  def receive: PartialFunction[Any, Unit] = {

    case SendHeartbeat => gameCounter match {
      case PRE_SET =>
        game.players.foreach(player => {
          playersList += player
        })
        controller.updateGUI(PlayersRef(playersList))
        gameCounter = START_SET

      case START_SET =>
        convertBriscolaSeed(currentSet.briscola)
        team1Score = currentSet.team1Score
        team2Score = currentSet.team2Score

        currentSet.playersHand.foreach(player => {
          if (playersList.head.equals(player._1))
            player._2.foreach(card => {
              var actualCard = CARD_PATH + card + CARD_FORMAT
              cardsListPlayer += actualCard
            })
        })

        controller.updateGUI(DistributedCard(cardsListPlayer.toList, playersList.head))
        controller.updateGUI(NotifyBriscolaChosen(briscolaChosen))
        currentHand = currentSet.hands.head
        currentMove = currentHand.moves.head
        gameCounter = MIDDLE_SET

      case MIDDLE_SET =>
        val actualCard = CARD_PATH + currentMove.card + CARD_FORMAT
        controller.updateGUI(PlayedCard(currentMove.player, actualCard))
        try {
          currentMove = currentHand.moves(currentHand.moves.indexOf(currentMove) + 1)
        } catch {
          case _: Exception =>
            try {
              currentHand = currentSet.hands(currentSet.hands.indexOf(currentHand) + 1)
            } catch {
              case _: Exception =>
                gameCounter = END_SET
            }
        }

      case END_SET =>
        try {
          currentSet = game.turns(game.turns.indexOf(currentSet) + 1)
          if (team1Score > team2Score)
            controller.updateGUI(ComputePartialGameScore(playersList.head, playersList.head, playersList.head, team1Score, team2Score))
          else
            controller.updateGUI(ComputePartialGameScore(user, playersList.head, playersList.head, team1Score, team2Score))
          gameCounter = START_SET
        } catch {
          case _: Exception =>
            if (team1Score > team2Score)
              controller.updateGUI(ComputeFinalGameScore(playersList.head, playersList.head, playersList.head, team1Score, team2Score))
            else
              controller.updateGUI(ComputeFinalGameScore(user, playersList.head, playersList.head, team1Score, team2Score))
        }
    }
  }


  override
  def getUsername: String = {
    user
  }

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

  case object PRE_SET extends ReplayActorStatus {
    override val asString: String = "pre_set"
  }

  case object START_SET extends ReplayActorStatus {
    override val asString: String = "start_set"
  }

  case object MIDDLE_SET extends ReplayActorStatus {
    override val asString: String = "middle_set"
  }

  case object END_SET extends ReplayActorStatus {
    override val asString: String = "end_set"
  }


  /**
    * This method is used to get all the available status.
    *
    * @return a Iterable containing all the available status.
    */
  def values: Iterable[ReplayActorStatus] = Iterable(PRE_SET, START_SET, MIDDLE_SET, END_SET)
}
