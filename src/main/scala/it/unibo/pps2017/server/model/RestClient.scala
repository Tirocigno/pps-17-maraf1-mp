
package it.unibo.pps2017.server.model

import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{HttpRequest, HttpResponse, WebClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Esegue una richiesta GET.
  *
  * @param uri
  * URL relativo
  * @param params
  * Parametri da fornire alla richiesta
  * @param onSuccess
  * Metodo che gestisce la risposta positiva
  * @param onFail
  * Metodo che gestisce la risposta negativa
  */
case class GETReq(url: String, port: Int = 0, uri: String, onSuccess: HttpResponse[Buffer] => Unit,
                  onFail: Throwable => Unit, params: Map[String, String] = null, vertx: Vertx = null) {
  var client : WebClient = _

  if (vertx == null) {
    client = WebClient.create(Vertx.vertx())
  } else {
    client = WebClient.create(vertx)
  }

  val complexUri = new StringBuffer(uri)
  var first: Boolean = true
  if (params != null) {
    params foreach { case (k, v) =>
      if (first) {
        complexUri.append("?" + k + "=" + v)
        first = false
      } else {
        complexUri.append("&" + k + "=" + v)
      }
    }
  }

  val future: HttpRequest[Buffer] = if (port != 0) client.get(port, url, complexUri.toString) else client.get(url, complexUri.toString)
  future.sendFuture().onComplete {
    case Success(result) =>
      onSuccess(result)
    case Failure(cause) =>
      onFail(cause)
  }

}