package it.unibo.pps2017.server.controller

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class UserDispatcher() {

  def addUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val db = res.getDatabaseConnection

    val username = ctx.request().getParam("username") match {
      case Some(name) => name
      case None => res.setGenericError(Some("Username not valid!")).sendResponse(Error())
    }

    db.hmset("user:" + username, ctx.request().formAttributes().add("score", "0"))
      .onComplete(queryRes => {
        if (queryRes.isSuccess) {
          res.sendResponse(Message("User entered correctly!"))
        } else {
          res.setGenericError(Some("Error on user registration!")).sendResponse(Error())
        }
      })
  }

  def getUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val db = res.getDatabaseConnection

    val username = ctx.request().getParam("username") match {
      case Some(name) => name
      case None => res.setGenericError(Some("Username not valid!")).sendResponse(Error())
    }

    db.exists("user:" + username)
      .onComplete {
        case Success(founded) =>
          if (founded) {
            db.hgetall("user:" + username).onComplete {
              case Success(values) =>
                res.sendResponse(User(username.toString, values("score").utf8String.toInt))
              case Failure(cause) =>
                res.setGenericError(Some(s"Unexpected error on user retrieve! Details: ${cause.getMessage}")).sendResponse(Error())
            }
          } else {
            res.setGenericError(Some(s"User ${username.toString} not found!")).sendResponse(Error())
          }
        case Failure(cause) =>
          res.setGenericError(Some(s"Unexpected error on user retrieve! Details: ${cause.getMessage}")).sendResponse(Error())
      }
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
    }

    val friend: String = params("friend")

    db.exists("user:" + username)
      .onComplete {
        case Success(founded) =>
          if (founded) {
            db.exists("user:" + friend)
              .onComplete {
                case Success(friendFounded) =>
                  if (friendFounded) {
                    addFriendship()
                  } else {
                    res.setGenericError(Some(s"Friend ${username.toString} not found!")).sendResponse(Error())
                  }
              }
          } else {
            res.setGenericError(Some(s"User ${username.toString} not found!")).sendResponse(Error())
          }
        case Failure(cause) =>
          res.setGenericError(Some(s"Unexpected error on user retrieve! Details: ${cause.getMessage}")).sendResponse(Error())
      }


    def addFriendship: () => Unit = () => {
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
}
