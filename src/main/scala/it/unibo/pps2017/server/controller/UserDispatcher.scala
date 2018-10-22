package it.unibo.pps2017.server.controller

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.database.RedisUserUtils
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse, User}

case class UserDispatcher() {

  val userDatabaseUtils = RedisUserUtils()

  def addUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val db = res.getDatabaseConnection

    val username = ctx.request().getParam("username") match {
      case Some(name) => name
      case None => res.setGenericError(Some("Username not valid!")).sendResponse(Error())
    }

    userDatabaseUtils.userSignIn(db,
      username.toString,
      ctx.request().formAttributes().add("score", "0"),
      _ => {
        res.sendResponse(Message("User entered correctly!"))
      }, cause => {
        res.setGenericError(Some(s"Error on user registration! Details: ${cause.getMessage}")).sendResponse(Error())
      })
  }

  def getUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val db = res.getDatabaseConnection

    val username = ctx.request().getParam("username") match {
      case Some(name) => name
      case None => res.setGenericError(Some("Username not valid!")).sendResponse(Error())
    }


    userDatabaseUtils.checkUserExisting(db, username.toString, queryRes => {
      if (queryRes) {
        userDatabaseUtils.getUser(db, username.toString,
          userData => {
            res.sendResponse(User(username.toString, userData("score").utf8String.toInt))
          }, cause => {
            res.setGenericError(Some(s"Unexpected error on user retrieve! Details: ${cause.getMessage}")).sendResponse(Error())
          })
      } else {
        res.setGenericError(Some(s"User ${username.toString} not found!")).sendResponse(Error())
      }
    }, cause => res.setGenericError(Some(s"Unexpected error on user retrieve! Details: ${cause.getMessage}")).sendResponse(Error()))

  }

  def addFriend: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val db = res.getDatabaseConnection

    val username = ctx.request().getParam("username") match {
      case Some(name) => name
      case None => res.setGenericError(Some("Username not valid!")).sendResponse(Error())
    }

    val params: Map[String, String] = ctx.request().formAttributes()

    if (!params.contains("friend")) {
      res.setGenericError(Some("You didn't specify a friend!")).sendResponse(Error())
    } else {

      val friend: String = params("friend")

      userDatabaseUtils.checkUserExisting(db, username.toString, userExist => {
        if (userExist) {
          userDatabaseUtils.checkUserExisting(db, friend, friendExist => {
            if (friendExist) {
              userDatabaseUtils.addFriendship(db, username.toString, friend, res)
            } else {
              res.setGenericError(Some(s"Friend ${friend.toString} not found!")).sendResponse(Error())
            }
          }, causeFriendError => {
            res
              .setGenericError(Some(s"Error on friend searching! Details: ${causeFriendError.getMessage}"))
              .sendResponse(Error())
          })
        } else {
          res.setGenericError(Some(s"User ${username.toString} not found!")).sendResponse(Error())
        }
      }, causeUserError => {
        res
          .setGenericError(Some(s"Unexpected error on user retrieve! Details: ${causeUserError.getMessage}"))
          .sendResponse(Error())
      })
    }
  }
}
