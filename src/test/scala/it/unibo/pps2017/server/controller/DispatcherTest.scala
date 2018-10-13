package it.unibo.pps2017.server.controller

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef
import it.unibo.pps2017.server.Runner
import it.unibo.pps2017.server.controller.DispatcherTest.PRIMARY_URL
import it.unibo.pps2017.server.model.ResponseStatus._
import org.json4s._

class DispatcherTest extends CornichonFeature {
  implicit val formats: DefaultFormats.type = DefaultFormats

  Runner.main(Seq("").toArray)

  override def feature: FeatureDef = Feature("TestRouting") {
    Scenario("hello") {
      When I get(PRIMARY_URL + "/")
      Then assert status.is(OK_CODE)
      And assert body.path("message").is("Hello to everyone")
    }

    Scenario("error") {
      When I get(PRIMARY_URL + "/error")
      Then assert status.is(EXCEPTION_CODE)
      And assert body.path("cause").is("Error")
    }


    Scenario("game") {
      When I get(PRIMARY_URL + "/game/jacopo")
      Then assert status.is(OK_CODE)
      And assert body.path("gameId").is("You write jacopo")
    }


  }
}

object DispatcherTest {
  val PRIMARY_URL: String = "http://" + Dispatcher.HOST + ":" + Dispatcher.PORT
}
