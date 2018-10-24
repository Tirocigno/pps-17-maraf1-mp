package it.unibo.pps2017.server.controller

import java.lang.Thread.sleep

import akka.actor.ActorSystem
import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.server.controller.DispatcherTest.PRIMARY_URL
import it.unibo.pps2017.server.controller.LobbyFoundTest.WAIT_TIMEOUT
import it.unibo.pps2017.server.controller.UserDispatcherTest.{friendForTest, usernameForTest}
import it.unibo.pps2017.server.model.ResponseStatus.{EXCEPTION_CODE, OK_CODE}
import it.unibo.pps2017.server.model.{Message, PostRequest}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class UserDispatcherTest extends CornichonFeature {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val verticle = Dispatcher(
    ActorSystem("Test"))

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

    Scenario("AddUserFriend") {
      When I post(PRIMARY_URL + "/user/addUser/" + friendForTest)
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


    Scenario("AddFriendshipWithError") {
      When I post(PRIMARY_URL + "/user/addFriend/" + usernameForTest)
      Then assert status.is(EXCEPTION_CODE)
      And assert body.path("cause").is(s"You didn't specify a friend!")
    }


  }
}

class AddFriendshipTest extends FunSuite with BeforeAndAfterEach {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val verticle = new Dispatcher(ActorSystem("Test"))
  var verticleId: Option[String] = None

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

  test("AddFriendship") {
    PostRequest(Dispatcher.HOST, "/user/addFriend/" + usernameForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)
      assert(msg.message == s"You and $friendForTest are now friends!")
    }, _ => {
      assert(false)
    }, Some(Map("friend" -> friendForTest)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }


  test("RemoveFriendship") {
    PostRequest(Dispatcher.HOST, "/user/removeFriend/" + usernameForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)

      assert(msg.message == s"You and $friendForTest are now unfamiliar!")
    }, _ => {
      assert(false)
    }, Some(Map("friend" -> friendForTest)), Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }
}


class DeleteUserTest extends CornichonFeature {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val verticle = Dispatcher(
    ActorSystem("Test"))

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

  override def feature: FeatureDef = Feature("CleanUsersForTest") {


    Scenario("GetFriends") {
      When I get(PRIMARY_URL + "/user/getFriends/" + usernameForTest)
      Then assert status.is(OK_CODE)
      And assert body.path("friends").isPresent
    }


    Scenario("DeleteUser") {
      When I post(PRIMARY_URL + "/user/removeUser/" + usernameForTest)
      Then assert status.is(OK_CODE)
      And assert body.path("message").is(s"User $usernameForTest deleted correctly!")
    }

    Scenario("DeleteUserFriend") {
      When I post(PRIMARY_URL + "/user/removeUser/" + friendForTest)
      Then assert status.is(OK_CODE)
      And assert body.path("message").is(s"User $friendForTest deleted correctly!")
    }
  }
}

object UserDispatcherTest {
  val usernameForTest = "ThisUserIsUsedForTest"
  val friendForTest = "ThisFriendIsUsedForTest"
}
