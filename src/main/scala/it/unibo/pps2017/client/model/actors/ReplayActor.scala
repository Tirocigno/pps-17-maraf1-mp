
package it.unibo.pps2017.client.model.actors

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ReplayActor.SendHeartbeat
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages.{DistributedCard, PlayersRef}
import it.unibo.pps2017.server.model.Game

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object ReplayActor {
  case class SendHeartbeat()
}

//class ReplayActor() extends Actor{

class ViewerActor(override val controller: GameController, username: String, game: Game) extends ClientGameActor {

  var user: String = "Nicholas"
  import system.dispatcher
  implicit val system = ActorSystem("ScheduledActors")
  private var i: Int = 0
  var cardsListPlayer = new ListBuffer[String]()
  var playersList = new ListBuffer[String]()
  final val FIRST_CARD: Int = 0
  final val LAST_CARD: Int = 10
  final val CARD_PATH: String = "cards/"
  final val CARD_FORMAT: String = ".png"


  override def preStart() {

    val cancellable =
      system.scheduler.schedule(
        0 milliseconds,
        3000 milliseconds,
        self,
        SendHeartbeat)
  }



  def receive: PartialFunction[Any, Unit] = {

    case SendHeartbeat =>
/*
      val gameBackup = new GameBackupImpl("TEST")
      val player1: String = "Nicholas"
      val player1Hand: Set[Card] = Set(CardImpl(Coin, 4), CardImpl(Coin, 3), CardImpl(Sword, 4), CardImpl(Club, 9))
      val player2: String = "Gjulio"
      val player2Hand: Set[Card] = Set(CardImpl(Coin, 10), CardImpl(Coin, 3), CardImpl(Sword, 4), CardImpl(Club, 9))
      val player3: String = "Federico"
      val player3Hand: Set[Card] = Set(CardImpl(Coin, 3), CardImpl(Sword, 3), CardImpl(Sword, 4), CardImpl(Cup, 9))
      val player4: String = "Jacopo"
      val player4Hand: Set[Card] = Set(CardImpl(Cup, 7), CardImpl(Sword, 3), CardImpl(Sword, 4), CardImpl(Club, 9))
      val playersInGame: Seq[String] = Seq(player1, player2, player3, player4)
      val playersHand = Map(player1 -> player1Hand, player2 -> player2Hand, player3 -> player3Hand, player4 -> player4Hand)
      val winners: Seq[String] = Seq(player1, player3)
      val moves: Seq[Move] = Seq(Move(player2, "Coin4"),
        Move(player3, "Coin4"),
        Move(player4, "Cup4"),
        Move(player1, "Coin3"))
      val hand = Hand(moves, player4)
      val gameSet: GameSet = GameSet(
        playersHand.map({ case (k, v) => (k, v.map(card => card.cardSeed.asString + card.cardValue)) }),
        Seq(hand), Sword.asString, 11, 0)
      val expectedGame: Game = Game(playersInGame, Seq(gameSet), winners)
*/

      // IF PER I QUATTRO GIOCATORI
      if (i == 0) {
        game.players.foreach(player => {
          playersList += player
        })
        controller.updateGUI(PlayersRef(playersList))

        // IF PER COSTRUIRE LA LISTA DELLE CARTE DI OGNI GIOCATORE
      } else if (i == 1) {
        game.turns.foreach(hand => {
          hand.playersHand.foreach(play => {
            if (user.equals(play._1))
              play._2.foreach(card => {
                var actualCard = CARD_PATH + card + CARD_FORMAT
                cardsListPlayer += actualCard
              })
          })
        })

        controller.updateGUI(DistributedCard(cardsListPlayer.toList, playersList.head))

      }



      i += 1


  }



  override
  def getUsername: String = {
    user
  }
}


/*
object ReplayActorApp extends App {
  val system = ActorSystem("SwapperSystem")
  val swap = system.actorOf(Props[ReplayActor], name = "replayActor")
  swap ! Swap
}*/