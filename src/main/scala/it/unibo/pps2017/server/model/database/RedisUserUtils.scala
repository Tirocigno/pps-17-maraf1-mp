package it.unibo.pps2017.server.model.database

import akka.util.ByteString
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

sealed trait UserDatabaseUtils {

  /**
    * Sign a new user in the database.
    *
    * @param username
    * new user id.
    * @param params
    * user data.
    * @param onSuccess
    * on user registration success.
    * @param onFail
    * error handler.
    */
  def userSignIn(username: String,
                 params: Map[String, String],
                 onSuccess: Boolean => Unit,
                 onFail: Throwable => Unit): Unit

  /**
    *
    * Search the presence of a user in the database.
    *
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  def checkUserExisting(username: String,
                        onSuccess: Boolean => Unit,
                        onFail: Throwable => Unit): Unit

  /**
    *
    * Check the correctness of the user's password.
    *
    * @param username
    * searched user.
    * @param password
    * user's password.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  def checkUserLogin(username: String,
                     password: String,
                     onSuccess: Boolean => Unit,
                     onFail: Throwable => Unit): Unit


  /**
    * Retrieve user data from the database.
    *
    * @param username
    * searched user.
    * @param onSuccess
    * on query success. Retrieve a Map with the user's data.
    * @param onFail
    * error handler.
    */
  def getUser(username: String,
              onSuccess: Map[String, ByteString] => Unit,
              onFail: Throwable => Unit): Unit


  /**
    * Remove an user from the database.
    *
    * @param username
    * user to be removed.
    * @param onSuccess
    * on user removed.
    * @param onFail
    * error handler.
    */
  def deleteUser(username: String,
                 onSuccess: Long => Unit,
                 onFail: Throwable => Unit): Unit


  /**
    * Add a friendship relation between user and friend.
    *
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  def addFriendship(username: String,
                    friend: String,
                    res: RouterResponse): Unit


  /**
    * Remove a friendship relation between user and friend.
    *
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  def removeFriendship(username: String,
                       friend: String,
                       res: RouterResponse): Unit


  /**
    * Search all friends of an user.
    *
    * @param username
    * user.
    * @param onSuccess
    * on friends founded.
    * @param onFail
    * error handler.
    */
  def getFriends(username: String,
                 onSuccess: Seq[String] => Unit,
                 onFail: Throwable => Unit): Unit


  /**
    * Increment the score of a player.
    *
    * @param username
    * player's id.
    * @param onSuccess
    * on success.
    * @param onFail
    * error handler.
    */
  def incrementScore(username: String,
                     onSuccess: Boolean => Unit,
                     onFail: Throwable => Unit): Unit

  /**
    * Decrement the score of a player.
    *
    * @param username
    * player's id.
    * @param onSuccess
    * on success.
    * @param onFail
    * error handler.
    */
  def decrementScore(username: String,
                     onSuccess: Boolean => Unit,
                     onFail: Throwable => Unit): Unit
}

/**
  * This class encapsulate some method for manage user in the database
  */
case class RedisUserUtils() extends UserDatabaseUtils {

  private def removeFriend(user: String, friend: String, onComplete: Future[Long] => Unit): Unit = {
    Query.withCallback(db =>
      db.srem(getUserFriendsKey(user), friend)
    )(onComplete)
  }

  private def addFriend(user: String, friend: String, onComplete: Future[Long] => Unit): Unit = {
    Query.withCallback(db =>
      db.sadd(getUserFriendsKey(user), friend)
    )(onComplete)
  }

  /**
    *
    * @param username
    * new user id.
    * @param params
    * user data.
    * @param onSuccess
    * on user registration success.
    * @param onFail
    * error handler.
    */
  def userSignIn(username: String,
                 params: Map[String, String],
                 onSuccess: Boolean => Unit,
                 onFail: Throwable => Unit): Unit = {

    Query(db =>
      db.hmset(getUserKey(username), params)
        .onComplete {
          case Success(queryRes) =>
            modifyScore(username, STARTING_SCORE, _ => {}, _ => {
              /*LOG ERROR*/
            })
            onSuccess(queryRes)
          case Failure(cause) => onFail(cause)
        }
    )
  }

  /**
    *
    * Search the presence of a user in the database.
    *
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  override def checkUserExisting(username: String,
                                 onSuccess: Boolean => Unit,
                                 onFail: Throwable => Unit): Unit = {

    Query(db =>
      db.exists(getUserKey(username))
        .onComplete {
          case Success(queryRes) => onSuccess(queryRes)
          case Failure(cause) => onFail(cause)
        }
    )
  }


  /**
    *
    * Check the correctness of the user's password.
    *
    * @param username
    * searched user.
    * @param password
    * user's password.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  override def checkUserLogin(username: String,
                              password: String,
                              onSuccess: Boolean => Unit,
                              onFail: Throwable => Unit): Unit = {

    Query(db =>
      db.hget(getUserKey(username), "password")
        .onComplete {
          case Success(value) =>
            value match {
              case Some(pw) => onSuccess(pw.utf8String.equalsIgnoreCase(password))
              case None => onSuccess(false)
            }
          case Failure(cause) =>
            onFail(cause)
        }
    )
  }

  /**
    * Retrieve user data from the database.
    *
    * @param username
    * searched user.
    * @param onSuccess
    * on query success.
    * @param onFail
    * error handler.
    */
  override def getUser(username: String,
                       onSuccess: Map[String, ByteString] => Unit,
                       onFail: Throwable => Unit): Unit = {
    Query(db =>
      db.hgetall(getUserKey(username))
        .onComplete {
          case Success(userData) => onSuccess(userData)
          case Failure(cause) => onFail(cause)
        }
    )
  }


  /**
    * Remove an user from the database.
    *
    * @param username
    * user to be removed.
    * @param onSuccess
    * on user removed.
    * @param onFail
    * error handler.
    */
  override def deleteUser(username: String,
                          onSuccess: Long => Unit,
                          onFail: Throwable => Unit): Unit = {
    Query(db =>
      db.del(getUserKey(username), getUserFriendsKey(username))
        .onComplete {
          case Success(queryRes) => onSuccess(queryRes) //TODO removing of all user references in friends list??
          case Failure(cause) => onFail(cause)
        }
    )
  }


  /**
    *
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  override def addFriendship(username: String, friend: String, res: RouterResponse): Unit = {
    addFriend(username.toString, friend, _.onComplete {
      case Success(userQueryRes) =>
        if (userQueryRes > 0) {
          addFriend(friend, username.toString, _.onComplete {
            case Success(friendQueryRes) =>
              if (friendQueryRes > 0) {
                res.sendResponse(Message(s"You and $friend are now friends!"))
              } else {
                removeFriend(username.toString, friend, _.onComplete(_ => {
                  res
                    .setGenericError(Some(s"You are already on the list of $friend's friends!"))
                    .sendResponse(Error())
                }))
              }
            case Failure(causeFriendQuery) =>
              removeFriend(username.toString, friend, _.onComplete(_ => {
                res
                  .setGenericError(Some(s"Unexpected error on user friendship adding! Details: ${causeFriendQuery.getMessage}"))
                  .sendResponse(Error())
              }))
          })
        } else {
          res
            .setGenericError(Some(s"You and $friend are already friends!"))
            .sendResponse(Error())
        }
      case Failure(causeUserQuery) =>
        res
          .setGenericError(Some(s"Unexpected error on user friendship adding! Details: ${causeUserQuery.getMessage}"))
          .sendResponse(Error())
    })
  }


  /**
    *
    * @param username
    * user.
    * @param friend
    * user's friend.
    * @param res
    * response to client.
    * This method has more case and error handler.
    * So it response directly to the client.
    */
  override def removeFriendship(username: String, friend: String, res: RouterResponse): Unit = {
    removeFriend(username.toString, friend, _.onComplete {
      case Success(userQueryRes) =>
        if (userQueryRes > 0) {
          removeFriend(friend, username.toString, _.onComplete {
            case Success(friendQueryRes) =>
              if (friendQueryRes > 0) {
                res.sendResponse(Message(s"You and $friend are now unfamiliar!"))
              } else {
                addFriend(username.toString, friend, _.onComplete(_ => {
                  res
                    .setGenericError(Some(s"You are not in the list of $friend's friends!"))
                    .sendResponse(Error())
                }))
              }
            case Failure(causeFriendQuery) =>
              addFriend(username.toString, friend, _.onComplete(_ => {
                res
                  .setGenericError(Some(s"Unexpected error on user friendship removing! Details: ${causeFriendQuery.getMessage}"))
                  .sendResponse(Error())
              }))
          })
        } else {
          res
            .setGenericError(Some(s"You and $friend are not friends!"))
            .sendResponse(Error())
        }
      case Failure(causeUserQuery) =>
        res
          .setGenericError(Some(s"Unexpected error on user friendship adding! Details: ${causeUserQuery.getMessage}"))
          .sendResponse(Error())
    }
    )
  }


  /**
    * Search all friends of an user.
    *
    * @param username
    * user.
    * @param onSuccess
    * on friends founded.
    * @param onFail
    * error handler.
    */
  override def getFriends(username: String,
                          onSuccess: Seq[String] => Unit,
                          onFail: Throwable => Unit): Unit = {

    Query(db =>
      db.smembers(getUserFriendsKey(username))
        .onComplete {
          case Success(friends) => {

            onSuccess(friends.map(_.utf8String))
          }
          case Failure(cause) => onFail(cause)
        }
    )
  }

  /**
    * Increment the score of a player.
    *
    * @param username
    * player's id.
    * @param onSuccess
    * on success.
    * @param onFail
    * error handler.
    */
  override def incrementScore(username: String, onSuccess: Boolean => Unit, onFail: Throwable => Unit): Unit = {
    modifyScore(username, SCORE_INC, onSuccess, onFail)
  }

  /**
    * Decrement the score of a player.
    *
    * @param username
    * player's id.
    * @param onSuccess
    * on success.
    * @param onFail
    * error handler.
    */
  override def decrementScore(username: String, onSuccess: Boolean => Unit, onFail: Throwable => Unit): Unit = {
    modifyScore(username, SCORE_DECR, onSuccess, onFail)
  }


  private def modifyScore(username: String, score: Long, onSuccess: Boolean => Unit, onFail: Throwable => Unit): Unit = {
    val db = RedisConnection().getDatabaseConnection

    val transaction = db.transaction()

    transaction.hincrby(getUserKey(username), USER_SCORE_KEY, score)
    transaction.zincrby(RANKING_KEY, score, username)

    transaction.exec()
      .onComplete {
        case Success(_) =>
          db.quit()
          transaction.quit()
          onSuccess(true)
        case Failure(cause) =>
          db.quit()
          transaction.quit()
          onFail(cause)
      }
  }
}
