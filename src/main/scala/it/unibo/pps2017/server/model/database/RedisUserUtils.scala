package it.unibo.pps2017.server.model.database

import akka.util.ByteString
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse}
import redis.RedisClient

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait UserDatabaseUtils {

  /**
    * Sign a new user in the database.
    *
    * @param db
    * database connection.
    * @param username
    * new user id.
    * @param params
    * user data.
    * @param onSuccess
    * on user registration success.
    * @param onFail
    * error handler.
    */
  def userSignIn(db: RedisClient,
                 username: String,
                 params: Map[String, String],
                 onSuccess: Boolean => Unit,
                 onFail: Throwable => Unit): Unit

  /**
    *
    * Search the presence of a user in the database.
    *
    * @param db
    * database connection.
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  def checkUserExisting(db: RedisClient,
                        username: String,
                        onSuccess: Boolean => Unit,
                        onFail: Throwable => Unit): Unit


  /**
    * Retrieve user data from the database.
    *
    * @param db
    * database connection.
    * @param username
    * searched user.
    * @param onSuccess
    * on query success. Retrieve a Map with the user's data.
    * @param onFail
    * error handler.
    */
  def getUser(db: RedisClient,
              username: String,
              onSuccess: Map[String, ByteString] => Unit,
              onFail: Throwable => Unit): Unit


  /**
    * Add a friendship relation between user and friend.
    *
    * @param db
    * database connection.
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  def addFriendship(db: RedisClient,
                    username: String,
                    friend: String,
                    res: RouterResponse): Unit
}

/**
  * This class encapsulate some method for manage user in the database
  */
case class RedisUserUtils() extends UserDatabaseUtils {

  /**
    *
    * @param db
    * database connection.
    * @param username
    * new user id.
    * @param params
    * user data.
    * @param onSuccess
    * on user registration success.
    * @param onFail
    * error handler.
    */
  def userSignIn(db: RedisClient,
                 username: String,
                 params: Map[String, String],
                 onSuccess: Boolean => Unit,
                 onFail: Throwable => Unit): Unit = {
    db.hmset(getUserKey(username), params)
      .onComplete {
        case Success(queryRes) => onSuccess(queryRes)
        case Failure(cause) => onFail(cause)
      }
  }

  /**
    *
    * Search the presence of a user in the database.
    *
    * @param db
    * database connection.
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  override def checkUserExisting(db: RedisClient,
                                 username: String,
                                 onSuccess: Boolean => Unit,
                                 onFail: Throwable => Unit): Unit = {

    db.exists(getUserKey(username))
      .onComplete {
        case Success(queryRes) => onSuccess(queryRes)
        case Failure(cause) => onFail(cause)
      }
  }

  /**
    * Retrieve user data from the database.
    *
    * @param db
    * database connection.
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  override def getUser(db: RedisClient,
                       username: String,
                       onSuccess: Map[String, ByteString] => Unit,
                       onFail: Throwable => Unit): Unit = {
    db.hgetall(getUserKey(username))
      .onComplete {
        case Success(userData) => onSuccess(userData)
        case Failure(cause) => onFail(cause)
      }
  }


  /**
    *
    * @param db
    * database connection.
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  override def addFriendship(db: RedisClient, username: String, friend: String, res: RouterResponse): Unit = {
    def removeFriend(user: String, friend: String): Future[Long] = {
      db.srem("user:" + user + ":friends", friend)
    }

    def addFriend(user: String, friend: String): Future[Long] = {
      db.sadd("user:" + user + ":friends", friend)
    }

    addFriend(username.toString, friend)
      .onComplete {
        case Success(userQueryRes) =>
          if (userQueryRes > 0) {
            addFriend(friend, username.toString)
              .onComplete {
                case Success(friendQueryRes) =>
                  if (friendQueryRes > 0) {
                    res.sendResponse(Message(s"You and $friend are now friends!"))
                  } else {
                    removeFriend(username.toString, friend).onComplete(_ => {
                      res
                        .setGenericError(Some(s"You are already on the list of $friend's friends!"))
                        .sendResponse(Error())
                    })
                  }
                case Failure(causeFriendQuery) =>
                  removeFriend(username.toString, friend).onComplete(_ => {
                    res
                      .setGenericError(Some(s"Unexpected error on user friendship adding! Details: ${causeFriendQuery.getMessage}"))
                      .sendResponse(Error())
                  })
              }
          } else {
            res
              .setGenericError(Some(s"You and $friend are already friends!"))
              .sendResponse(Error())
          }
        case Failure(causeUserQuery) =>
          res
            .setGenericError(Some(s"Unexpected error on user friendship adding! Details: ${causeUserQuery.getMessage}"))
            .sendResponse(Error())
      }
  }
}
