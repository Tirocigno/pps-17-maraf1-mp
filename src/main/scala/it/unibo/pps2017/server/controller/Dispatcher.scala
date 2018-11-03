
package it.unibo.pps2017.server.controller


import akka.actor.{ActorRef, ActorSystem, Props}
import io.vertx.core.http.HttpHeaders
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{RegisterServerAPI, StandardParameters}
import it.unibo.pps2017.server.actor.{LobbyActor, TriggerSearch}
import it.unibo.pps2017.server.controller.Dispatcher.{PORT, TIMEOUT}
import it.unibo.pps2017.server.model.GameType.{RANKED, UNRANKED}
import it.unibo.pps2017.server.model.ServerApi._
import it.unibo.pps2017.server.model._
import it.unibo.pps2017.server.model.database.{DatabaseUtils, RedisUtils}
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization.write

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object Dispatcher {
  val applicationJson: String = "application/json"
  val USER = "user:"
  var HOST: String = "localhost"
  val PORT: Int = 4700
  var PASSWORD: Option[String] = Some("")
  val RESULT = "result"
  val TIMEOUT = 1000

  private var discoveryUrl: String = "127.0.0.1"
  private var myIp: String = "127.0.0.1"

  def setDiscovery(value: String): Unit = discoveryUrl = value

  val DISCOVERY_PORT: Int = 2000

  def DISCOVERY_URL: String = discoveryUrl

  def setMyIp(value: String): Unit = myIp = value

  def MY_IP: String = myIp

  val VERTX = Vertx.vertx()
}


case class Dispatcher(actorSystem: ActorSystem) extends ScalaVerticle {

  implicit val akkaSystem: ActorSystem = actorSystem


  val userMethods = UserDispatcher()
  val gameMethods = GameDispatcher()

  val databaseUtils: DatabaseUtils = RedisUtils()

  val lobbyManager: ActorRef = akkaSystem.actorOf(Props[LobbyActor])
  val currentIPAndPortParams = Map(StandardParameters.IP_KEY -> Dispatcher.MY_IP, StandardParameters.PORT_KEY -> PORT)


  override def start(): Unit = {

    val router = Router.router(vertx)

    ServerApi.values.map({
      case api@HelloRestAPI => api.asRequest(router, hello)
      case api@ErrorRestAPI => api.asRequest(router, responseError)
      case api@GameRestAPI => api.asRequest(router, gameMethods.getGame)
      case api@FoundGameRestAPI => api.asRequest(router, foundGame)
      case api@AddUserAPI => api.asRequest(router, userMethods.addUser)
      case api@GetUserAPI => api.asRequest(router, userMethods.getUser)
      case api@LoginAPI => api.asRequest(router, userMethods.login)
      case api@RemoveUserAPI => api.asRequest(router, userMethods.deleteUser)
      case api@AddFriendAPI => api.asRequest(router, userMethods.addFriend)
      case api@GetFriendsAPI => api.asRequest(router, userMethods.getFriends)
      case api@RemoveFriendAPI => api.asRequest(router, userMethods.removeFriend)
      case api@GetLiveMatchAPI => api.asRequest(router, gameMethods.getLiveGames)
      case api@GetRankingAPI => api.asRequest(router, getRanking)
      case api@GetSavedMatchAPI => api.asRequest(router, gameMethods.getSavedMatches)
      case api@_ => api.asRequest(router, (_, res) => res.setGenericError(Some("RestAPI not founded.")).sendResponse(Error()))
    })


    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)


    var port = PORT

    if (System.getenv("PORT") != null) port = System.getenv("PORT").toInt

    router.route().handler(ctx => {
      val err = Error(Some(s"Error 404 not found"))

      ctx.response().setStatusCode(404)
      ctx.response().putHeader(HttpHeaders.CONTENT_TYPE.toString, "application/json; charset=utf-8")
      ctx.response().end(write(err))
    })

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(port)


    akkaSystem.scheduler.scheduleOnce(3 second) {
      PostRequest(Dispatcher.DISCOVERY_URL, RegisterServerAPI.path, {
        case Some(res) => try {
          val msgFromDiscovery = read[Message](res)

          println("Discovery registration response: " + msgFromDiscovery.message)
        } catch {
          case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
        }
        case None => println("No response from the discovery")
      }, cause => {
        println("Error on the discovery registration! \nDetails: " + cause.getMessage)
      }, Some(currentIPAndPortParams), Some(Dispatcher.DISCOVERY_PORT))
    }


  }

  /**
    * Welcome response.
    */
  private val hello: (RoutingContext, RouterResponse) => Unit = (_, res) => {
    res.sendResponse(Message("Hello to everyone"))
  }


  /**
    * Respond to POST /foundGame
    *
    */
  private val foundGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {
    val params = routingContext.request().formAttributes()

    val player = params.get(FoundGameRestAPI.meParamKey)
    val friend = params.get(FoundGameRestAPI.partnerParam)

    val vs = params.get(FoundGameRestAPI.vsParam)
    val vsPartner = params.get(FoundGameRestAPI.vsPartnerParam)

    val isRanked: Boolean = params.get("ranked").getOrElse("").equals("true")

    val gameFoundEvent: String => Unit = gameId => {
      res.sendResponse(GameFound(gameId))
    }


    val team1: ListBuffer[String] = ListBuffer()
    val team2: ListBuffer[String] = ListBuffer()


    player match {
      case Some(id) =>
        team1 += id
        friend.map(team1 += _)

        vs.map(team2 += _)
        vsPartner.map(team2 += _)

        if (isRanked) {
          lobbyManager ! TriggerSearch(team1, team2, gameFoundEvent, RANKED)
        } else {
          lobbyManager ! TriggerSearch(team1, team2, gameFoundEvent, UNRANKED)
        }
      case None =>
        errorHandler(res, "Id player not specified in the request")
    }
  }


  /**
    * Get Ranking.
    */
  private val getRanking: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val from: Option[Long] = try {
      ctx.queryParams().get(GetRankingAPI.fromKey).map(_.toLong)
    } catch {
      case _: Exception => None
    }

    val to: Option[Long] = try {
      ctx.queryParams().get(GetRankingAPI.toKey).map(_.toLong)
    } catch {
      case _: Exception => None
    }

    databaseUtils.getRanking(elements => {
      val result: ListBuffer[RankElement] = ListBuffer()

      elements.foreach(e => result += RankElement(e._1, e._2.toLong))

      res.sendResponse(Ranking(result))
    }, cause => {
      errorHandler(res, s"Error on retrieve the ranking. \nDetails: ${cause.getMessage}")
    }, from, to)
  }

  /**
    * Respond with a generic error message.
    *
    */
  private val responseError: (RoutingContext, RouterResponse) => Unit = (_, res) => {

    res.setGenericError(Some("Error"))
      .sendResponse(Error())
  }
}