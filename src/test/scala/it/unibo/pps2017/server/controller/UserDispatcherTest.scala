package it.unibo.pps2017.server.controller

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.DispatcherTest.PRIMARY_URL
import it.unibo.pps2017.server.controller.UserDispatcherTest.usernameForTest
import it.unibo.pps2017.server.model.ResponseStatus.{EXCEPTION_CODE, OK_CODE}
import org.json4s.DefaultFormats

import scala.util.{Failure, Success}

class UserDispatcherTest extends CornichonFeature {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val verticle = Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      Dispatcher.DISCOVERY_URL,
      "0",
      Dispatcher.MY_IP))

  var verticleId: Option[String] = None


  beforeFeature {
    Vertx.vertx().deployVerticleFuture(verticle).onComplete {
      case Success(res) => verticleId = Some(res)
      case Failure(cause) => println("Error on verticle deployment, cause: " + cause)
    }
  }

  afterFeature {
    verticleId match {
      case Some(id) => Vertx.vertx().undeploy(id)
      case None => println("No verticle to close!")
    }
  }

  override def feature: FeatureDef = Feature("AddAngGetUserTest") {
    Scenario("Add") {
      When I post(PRIMARY_URL + "/user/addUser/" + usernameForTest)
      Then assert status.is(OK_CODE)
      And assert body.path("message").is("User entered correctly!")
    }

    Scenario("ErrorOnGet") {
      When I get(PRIMARY_URL + "/user/NonExisting" + usernameForTest)
      Then assert status.is(EXCEPTION_CODE)
      And assert body.path("cause").is(s"User NonExisting$usernameForTest not found!")
    }

    Scenario("Get") {
      When I get(PRIMARY_URL + "/user/" + usernameForTest)
      Then assert status.is(OK_CODE)
      And assert body.path("username").is(usernameForTest)
      And assert body.path("score").is(0)
    }
  }
}

object UserDispatcherTest {
  val usernameForTest = "ThisUserIsUsedForTest"

}
