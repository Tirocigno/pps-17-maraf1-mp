package it.unibo.pps2017.commons.model.database

import akka.actor.ActorSystem
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup, Sword}
import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}
import it.unibo.pps2017.server.model.database.RedisGameUtils
import it.unibo.pps2017.server.model.{Game, GameSet, Hand, Move}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class GameBackupImplTest extends FunSuite with Matchers {


  test("GameStoring") {
    val gameBackup = new GameBackupImpl("TEST")


    val player1: String = "player1"
    val player1Hand: Set[Card] = Set(CardImpl(Coin, 4), CardImpl(Coin, 3), CardImpl(Sword, 4), CardImpl(Club, 9))

    val player2: String = "player2"
    val player2Hand: Set[Card] = Set(CardImpl(Coin, 4), CardImpl(Coin, 3), CardImpl(Sword, 4), CardImpl(Club, 9))

    val player3: String = "player3"
    val player3Hand: Set[Card] = Set(CardImpl(Coin, 4), CardImpl(Sword, 3), CardImpl(Sword, 4), CardImpl(Cup, 9))

    val player4: String = "player4"
    val player4Hand: Set[Card] = Set(CardImpl(Cup, 4), CardImpl(Sword, 3), CardImpl(Sword, 4), CardImpl(Club, 9))

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


    gameBackup.startGame(playersInGame)
    gameBackup.startSet(playersHand, Sword)
    gameBackup.startHand()
    gameBackup.addMove(player2, CardImpl(Coin, 4))
    gameBackup.addMove(player3, CardImpl(Coin, 4))
    gameBackup.addMove(player4, CardImpl(Cup, 4))
    gameBackup.addMove(player1, CardImpl(Coin, 3))
    gameBackup.endHand(player4)
    gameBackup.endSet(11, 0)
    gameBackup.endGame(winners)


    val result = Await.result(RedisGameUtils().getGame("TEST"), 500 millis)

    result shouldBe Some(expectedGame)
  }
}
