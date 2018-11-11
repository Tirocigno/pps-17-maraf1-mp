package it.unibo.pps2017.server.model.database.base

import akka.util.ByteString
import it.unibo.pps2017.server.model.RouterResponse

trait UserDatabaseInterface {

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
