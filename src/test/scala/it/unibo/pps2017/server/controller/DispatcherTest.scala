
package it.unibo.pps2017.server.controller

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.server.controller.DispatcherTest.{GAME_ID, PRIMARY_URL}
import it.unibo.pps2017.server.model.ResponseStatus._
import org.json4s._

import scala.util.{Failure, Success}

object DispatcherTest {
  val GAME_ID: String = "gameId"
  val PRIMARY_URL: String = "http://" + Dispatcher.HOST + ":" + Dispatcher.PORT
}

class DispatcherTest extends CornichonFeature {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val verticle = new Dispatcher()
  var verticleId: Option[String] = None
  var lobbyId: String = _
  beforeFeature {
    Vertx.vertx().deployVerticleFuture(verticle).onComplete {
      case Success(res) => verticleId = Some(res)
      case Failure(cause) => println("Error on verticle deployment, cause: " + cause)
    }
  }

  afterFeature {
    verticleId match {
      case Some(id) => Vertx.vertx().undeploy(id)
    }
  }

  override def feature: FeatureDef = Feature("TestRouting") {
    Scenario("Hello") {
      When I get(PRIMARY_URL + "/")
      Then assert status.is(OK_CODE)
      And assert body.path("message").is("Hello to everyone")
    }

    Scenario("Error") {
      When I get(PRIMARY_URL + "/error")
      Then assert status.is(EXCEPTION_CODE)
      And assert body.path("cause").is("Error")
    }


    Scenario("GetGame") {
      When I get(PRIMARY_URL + "/game/jacopo")
      Then assert status.is(OK_CODE)
      And assert body.path(GAME_ID).is("jacopo")
    }


    Scenario("SearchingLobby") {
      When I post(PRIMARY_URL + "/foundGame").withParams("wrongParam" -> "jacopo")
      And assert status.is(EXCEPTION_CODE)
    }
  }
}

