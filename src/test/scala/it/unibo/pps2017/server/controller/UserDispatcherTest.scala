package it.unibo.pps2017.server.controller

import java.lang.Thread._

import akka.actor.ActorSystem
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.server.controller.UserDispatcherTest._
import it.unibo.pps2017.server.model.{Error, GetRequest, Message, PostRequest, User, UserFriends}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class UserDispatcherTest extends FunSuite with BeforeAndAfterAll {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val verticle = new Dispatcher(ActorSystem("Test"))
  var verticleId: Option[String] = None

  override protected def beforeAll(): Unit = {
    Vertx.vertx().deployVerticleFuture(verticle).onComplete {
      case Success(res) => verticleId = Some(res)
      case Failure(cause) => println("Error on verticle deployment, cause: " + cause)
    }
  }

  override protected def afterAll(): Unit = {
    verticleId match {
      case Some(id) => Vertx.vertx().undeploy(id)
      case None =>
    }
  }


  test("Add") {
    PostRequest(Dispatcher.HOST, "/user/addUser/" + usernameForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)
      assert(msg.message == "User entered correctly!")
    }, cause => {
      cause.printStackTrace()
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }


  test("AddUserFriend") {
    PostRequest(Dispatcher.HOST, "/user/addUser/" + friendForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)
      assert(msg.message == "User entered correctly!")
    }, cause => {
      cause.printStackTrace()
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }


  test("ErrorOnGet") {
    GetRequest(Dispatcher.HOST, "/user/NonExisting" + usernameForTest, onSuccess => {
      val error = read[Error](onSuccess.get)
      assert(error.cause.get == s"User NonExisting$usernameForTest not found!")
    }, cause => {
      cause.printStackTrace()
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }

  test("Get") {
    GetRequest(Dispatcher.HOST, "/user/" + usernameForTest, onSuccess => {
      val user = read[User](onSuccess.get)
      assert(user.username == usernameForTest)
      assert(user.score == 200)
    }, cause => {
      cause.printStackTrace()
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }


  test("AddFriendshipWithError") {
    PostRequest(Dispatcher.HOST, "/user/addFriend/" + usernameForTest, onSuccess => {
      val error = read[Error](onSuccess.get)
      assert(error.cause.get == "You didn't specify a friend!")
    }, _ => {
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
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

  test("GetFriends") {
    GetRequest(Dispatcher.HOST, "/user/getFriends/" + usernameForTest, onSuccess => {
      val userWithFriends = read[UserFriends](onSuccess.get)

      assert(userWithFriends.friends.head == friendForTest)
    }, cause => {
      assert(false)
    }, None, Some(Dispatcher.PORT))

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

  test("DeleteUser") {
    PostRequest(Dispatcher.HOST, "/user/removeUser/" + usernameForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)

      assert(msg.message == s"User $usernameForTest deleted correctly!")
    }, _ => {
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }

  test("DeleteUserFriend") {
    PostRequest(Dispatcher.HOST, "/user/removeUser/" + friendForTest, onSuccess => {
      val msg = read[Message](onSuccess.get)

      assert(msg.message == s"User $friendForTest deleted correctly!")
    }, _ => {
      assert(false)
    }, None, Some(Dispatcher.PORT))

    sleep(WAIT_TIMEOUT)
  }

}


object UserDispatcherTest {

  val WAIT_TIMEOUT: Int = 1000
  val usernameForTest = "ThisUserIsUsedForTest"
  val friendForTest = "ThisFriendIsUsedForTest"
}
