package it.unibo.pps2017.server.controller

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.database.RedisUser
import it.unibo.pps2017.server.model.{Message, RouterResponse, User, UserFriends}

case class UserDispatcher() {

  val userDatabaseUtils = RedisUser()

  def addUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val username = getUsernameOrResponseError(ctx, res)

    userDatabaseUtils.checkUserExisting(username.toString, queryRes => {
      if (queryRes) {
        errorHandler(res, s"User $username already signed in!")
      } else {
        userDatabaseUtils.userSignIn(username.toString,
          ctx.request().formAttributes().add("score", "0"),
          _ => {
            res.sendResponse(Message("User entered correctly!"))
          }, cause => {
            errorHandler(res, s"Error on user registration! Details: ${cause.getMessage}")
          })
      }
    }, cause => errorHandler(res, s"Unexpected error on user presence checking! Details: ${cause.getMessage}"))


  }

  def getUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {

    val username = getUsernameOrResponseError(ctx, res)


    userDatabaseUtils.checkUserExisting(username.toString, queryRes => {
      if (queryRes) {
        userDatabaseUtils.getUser(username.toString,
          userData => {
            res.sendResponse(User(username.toString, userData("score").utf8String.toInt))
          }, cause => {
            errorHandler(res, s"Unexpected error on user retrieve! Details: ${cause.getMessage}")
          })
      } else {
        errorHandler(res, s"User $username not found!")
      }
    }, cause => errorHandler(res, s"Unexpected error on user retrieve! Details: ${cause.getMessage}"))

  }

  def login: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val username = getUsernameOrResponseError(ctx, res)
    val password: String = ctx.request().formAttributes().get("password").getOrElse("")

    userDatabaseUtils.checkUserExisting(username.toString, exist => {
      if (exist) {
        userDatabaseUtils.checkUserLogin(username.toString, password, result => {
          if (result) {
            res.sendResponse(Message(s"$username, you are welcome!"))
          } else {
            errorHandler(res, "Invalid username or password!")
          }
        }, cause => {
          errorHandler(res, s"Unexpected error on user login check! Details: ${cause.getMessage}")
        })
      } else {
        errorHandler(res, s"User $username not found!")
      }
    }, cause => {
      errorHandler(res, s"Unexpected error on user retrieve! Details: ${cause.getMessage}")
    })
  }

  def deleteUser: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val username = getUsernameOrResponseError(ctx, res)

    userDatabaseUtils.deleteUser(username.toString, removedKeys => {
      if (removedKeys > 0) {
        res.sendResponse(Message(s"User $username deleted correctly!"))
      } else {
        errorHandler(res, s"User $username not found!")
      }
    }, cause => {
      errorHandler(res, s"Unexpected error on user deleting! Details: ${cause.getMessage}")
    })
  }

  def addFriend: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val username = getUsernameOrResponseError(ctx, res)

    val params: Map[String, String] = ctx.request().formAttributes()

    if (!params.contains("friend")) {
      errorHandler(res, "You didn't specify a friend!")
    } else {

      val friend: String = params("friend")

      userDatabaseUtils.checkUserExisting(username.toString, userExist => {
        if (userExist) {
          userDatabaseUtils.checkUserExisting(friend, friendExist => {
            if (friendExist) {
              userDatabaseUtils.addFriendship(username.toString, friend, res)
            } else {
              errorHandler(res, s"Friend $friend not found!")
            }
          }, causeFriendError => {
            errorHandler(res, s"Error on friend searching! Details: ${causeFriendError.getMessage}")
          })
        } else {
          errorHandler(res, s"User $username not found!")
        }
      }, causeUserError => {
        errorHandler(res, s"Unexpected error on user retrieve! Details: ${causeUserError.getMessage}")
      })
    }
  }

  def getFriends: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {

    val username = getUsernameOrResponseError(ctx, res)

    userDatabaseUtils.checkUserExisting(username.toString, exist => {
      if (exist) {
        userDatabaseUtils.getFriends(username.toString,
          friends => {
            res.sendResponse(UserFriends(username.toString, friends))
          }, cause => {
            errorHandler(res, s"Unexpected error on friends retrieve! Details: ${cause.getMessage}")
          })
      } else {
        errorHandler(res, s"User $username not found!")
      }
    }, cause => {
      errorHandler(res, s"Unexpected error on user retrieve! Details: ${cause.getMessage}")
    })
  }


  def removeFriend: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val username = getUsernameOrResponseError(ctx, res)

    val params: Map[String, String] = ctx.request().formAttributes()

    if (!params.contains("friend")) {
      errorHandler(res, "You didn't specify a friend!")
    } else {

      val friend: String = params("friend")

      userDatabaseUtils.checkUserExisting(username.toString, userExist => {
        if (userExist) {
          userDatabaseUtils.checkUserExisting(friend, friendExist => {
            if (friendExist) {
              userDatabaseUtils.removeFriendship(username.toString, friend, res)
            } else {
              errorHandler(res, s"Friend ${friend.toString} not found!")
            }
          }, causeFriendError => {
            errorHandler(res, s"Error on friend searching! Details: ${causeFriendError.getMessage}")
          })
        } else {
          errorHandler(res, s"User ${username.toString} not found!")
        }
      }, causeUserError => {
        errorHandler(res, s"Unexpected error on user retrieve! Details: ${causeUserError.getMessage}")
      })
    }
  }


  private def getUsernameOrResponseError(ctx: RoutingContext, res: RouterResponse): Any = {
    ctx.request().getParam("username") match {
      case Some(name) => name
      case None => errorHandler(res, "Username not valid!")
    }
  }
}
