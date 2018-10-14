
package it.unibo.pps2017.server.controller

import java.lang.Thread.sleep

import io.vertx.scala.core.Vertx
import it.unibo.pps2017.server.controller.LobbyFoundTest.{FOUND_GAME_URI, ME_PARAM, PARTNER_PARAM, WAIT_TIMEOUT}
import it.unibo.pps2017.server.model.{GameFound, PostRequest}
import org.json4s._
import org.json4s.jackson.Serialization.read
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


/**
  * The lobby tester.
  * Try to add more player or team in searching of a game and matching the responses.
  */
class LobbyFoundTest extends FunSuite with BeforeAndAfterEach {

  implicit val formats: DefaultFormats.type = DefaultFormats

  val verticle = new Dispatcher()
  var verticleId: Option[String] = None
  var lobbyId: String = _
  val player1 = "player1"
  val player2 = "player2"
  val player3 = "player3"
  val player4 = "player4"

  override protected def beforeEach(): Unit = {
    Vertx.vertx().deployVerticleFuture(verticle).onComplete {
      case Success(res) => verticleId = Some(res)
      case Failure(cause) => println("Error on verticle deployment, cause: " + cause)
    }
  }

  override protected def afterEach(): Unit = {
    verticleId match {
      case Some(id) => Vertx.vertx().undeploy(id)
      case None =>
    }
  }

  test("4PlayerSearchingLobby") {
    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      lobbyId = game.gameId
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player1)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId == game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player2)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId == game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player3)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId == game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player4)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId != game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player4)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }


  test("1TeamAnd2PlayerSearchingLobby") {
    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      lobbyId = game.gameId
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player1, PARTNER_PARAM -> player2)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId == game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player3)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      lobbyId = game.gameId
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player3, PARTNER_PARAM -> player2)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)


    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId == game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player4)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      assert(lobbyId != game.gameId)
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player3, PARTNER_PARAM -> player2)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)

    PostRequest(Dispatcher.HOST, FOUND_GAME_URI, res => {
      val game = read[GameFound](res.get)
      lobbyId = game.gameId
    }, cause => println(cause.getMessage), Some(Map(ME_PARAM -> player3)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }
}

object LobbyFoundTest {
  val WAIT_TIMEOUT: Int = 1000
  val FOUND_GAME_URI: String = "/foundGame"
  val ME_PARAM: String = "me"
  val PARTNER_PARAM: String = "partner"
}
