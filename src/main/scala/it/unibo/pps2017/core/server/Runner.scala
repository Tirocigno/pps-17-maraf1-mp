package it.unibo.pps2017.core.server
import controller.Dispatcher
import io.vertx.scala.core.Vertx



object Runner extends App {
  Vertx.vertx().deployVerticle(new Dispatcher())
}

