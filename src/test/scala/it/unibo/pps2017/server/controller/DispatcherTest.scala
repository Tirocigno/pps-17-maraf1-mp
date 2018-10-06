package it.unibo.pps2017.server.controller

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef
import it.unibo.pps2017.server.Runner
import it.unibo.pps2017.server.controller.DispatcherTest.PRIMARY_URL
import it.unibo.pps2017.server.model.ResponseStatus._

class DispatcherTest extends CornichonFeature {
  Runner.main(Seq("").toArray)

  override def feature: FeatureDef = Feature("TestRouting") {
    Scenario("hello") {
      When I get(PRIMARY_URL + "/")
      Then assert status.is(OK_CODE)
      And assert body.path("Message.message").is("Hello to everyone")
    }

    Scenario("error") {
      When I get(PRIMARY_URL + "/error")
      Then assert status.is(EXCEPTION_CODE)
      And assert body.path("cause").is("Error")
    }
  }
}

object DispatcherTest {
  val PRIMARY_URL: String = "http://" + Dispatcher.HOST + ":" + Dispatcher.PORT
}
