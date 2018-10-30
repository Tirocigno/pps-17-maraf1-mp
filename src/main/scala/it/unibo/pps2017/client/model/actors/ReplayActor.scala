
package it.unibo.pps2017.client.model.actors

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ReplayActor.SendHeartbeat
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages._
import it.unibo.pps2017.core.deck.cards.Seed._
import it.unibo.pps2017.server.model.{Game, GameSet, Hand, Move}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object ReplayActor {

  case class SendHeartbeat()

}

//class ReplayActor() extends Actor {

class ReplayActor(override val controller: GameController, username: String, game: Game) extends ClientGameActor {

  var user: String = username

  import system.dispatcher

  val system = ActorSystem("ScheduledActors")

  var gameCounter: Int = 0

  var cardsListPlayer = new ListBuffer[String]()
  var playersList = new ListBuffer[String]()
  var briscolaChosen: Seed = _

  /* due variabili per gestire il momento in cui devo recuperare le carte del primo utente */
  var counterTurn = 1
  var counterForPlayerHand = 0

  /* le due variabili per il punteggio delle due squadre */
  var team1Score = 0
  var team2Score = 0

  final val FIRST_CARD: Int = 0
  final val LAST_CARD: Int = 10
  final val CARD_PATH: String = "cards/"
  final val CARD_FORMAT: String = ".png"


  //jacopo
  var currentSet: GameSet = _
  var currentHand: Hand = _
  var currentMove: Move = _


  override def preStart() {
    //Jacopo
    //currentSet = game.turns.head

    system.scheduler.schedule(
      initialDelay = 0 milliseconds,
      interval = 3000 milliseconds,
      receiver = self,
      message = SendHeartbeat)
  }


  def receive: PartialFunction[Any, Unit] = {

    case SendHeartbeat =>

      /**
        * Situazione zero. Recupero gli username dei quattro player.
        */
      if (gameCounter == 0) {
        game.players.foreach(player => {
          playersList += player
        })
        controller.updateGUI(PlayersRef(playersList))


        /**
          * Situazione uno. Recupero le carte in mano al primo giocatore.
           Il primo giro avro' counterForPlayerHand = 1 e counterTurn = 1, quindi prendo il primo turn.
           Il secondo giro avro' inizialmente counterForPlayerHand = 1 (azzerato alla fine del ciclo precedente e incrementato
           a 1 appena entrato) counterTurn = 2, quindi il primo ciclo non entro ma entro al secondo quando counterForPlayerHand = 2
           e cosi' via.
         */
      } else if (gameCounter == 1) {

        //JACOPO -> FORSE MEGLIO PULIRE USANDO CURRENT SET
        game.turns.foreach(hand => {
          counterForPlayerHand += 1
          if (counterForPlayerHand == counterTurn) {
            convertBriscolaSeed(hand.briscola) // prendo la briscola di questo turno
            team1Score = hand.team1Score // prendo il punteggio del primo team
            team2Score = hand.team2Score // prendo il punteggio del secondo team
            hand.playersHand.foreach(player => {
              if (user.equals(player._1))
                player._2.foreach(card => {
                  var actualCard = CARD_PATH + card + CARD_FORMAT
                  cardsListPlayer += actualCard
                })
            })
          }
        })
        counterTurn += 1
        counterForPlayerHand = 0
        controller.updateGUI(DistributedCard(cardsListPlayer.toList, playersList.head))
        controller.updateGUI(NotifyBriscolaChosen(briscolaChosen))




        /*
        JACOPO
        currentHand = currentSet.hands.head
        currentMove = currentHand.moves.head
        */

      } else if (gameCounter == 2) {

        /*
        JACOPO
        controller.updateGUI(SendMove(currentMove.player, currentMove.card))
        try {
          currentMove = currentHand.moves(currentHand.moves.indexOf(currentMove) + 1)
        } catch {
          case _: Exception =>
            try {
              currentHand = currentSet.hands(currentSet.hands.indexOf(currentHand) + 1)
            } catch {
              case _: Exception =>
                //E' finito il Set posso procedere con la visualizzazione del punteggio
                //gameCounter += 1
            }
        }*/

        /**
          * Terza situazione. Invio del punteggio. Visto che non posso sapere i winner se il punteggio del team1
          * (di cui faccio parte) e' maggiore del team2, invio come uno degli utenti che hanno vinto lo username
          * del primo player sia nel campo user che in quello winner1. Altrimenti invio lo username dell'attore
          * replay che non sara' mai uguale a quello del primo player.
          */
      } else if (gameCounter == 3) {

        /* Aggiungere controllo se la partita e' finita, altrimenti si torna alla fase di distribuzione carte */
        if (team1Score > team2Score) controller.updateGUI(ComputePartialGameScore(playersList.head, playersList.head, playersList.head, team1Score, team2Score))
        else controller.updateGUI(ComputePartialGameScore(user, playersList.head, playersList.head, team1Score, team2Score))

        /*
        JACOPO
        try {
          currentSet = game.turns(game.turns.indexOf(currentSet) + 1)
        } catch {
          case _: Exception =>
            //QUI SO CHE LA PARTITA è FINITA! AGIRE DI CONSEGUENZA
        }
        */

        //if !partitaFinita  gameCounter = 1
      }






      gameCounter += 1

  }


  override
  def getUsername: String = {
    user
  }

  /* da verificare se funziona bene, altrimenti inserire l'if */
  private def convertBriscolaSeed(briscola: String): Unit = briscola match {
    case Sword.asString => briscolaChosen = Sword
    case Cup.asString => briscolaChosen = Cup
    case Coin.asString => briscolaChosen = Coin
    case Club.asString => briscolaChosen = Club
  }
}

/*
object ReplayActorApp extends App {
  val system = ActorSystem("SwapperSystem")
  val swap = system.actorOf(Props[ReplayActor], name = "replayActor")
  swap ! Swap
}*/