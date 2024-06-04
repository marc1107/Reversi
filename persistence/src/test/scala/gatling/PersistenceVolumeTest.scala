package gatling

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import akka.http.javadsl.Http
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.structure.PopulationBuilder
import io.gatling.core.body.Body
import akka.http.javadsl.model.HttpMethod

class PersistenceVolumeTest extends SimulationSkeleton {

  override val operations = List(
    buildOperation(
      "persistence save",
      "POST",
      "/fileio",
      ElFileBody("game-vol.json")
    ),
    buildOperation(
      "persistence load",
      "GET",
      "/fileio/load",
      StringBody("")
    )
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

    setUp(
      scn
        .inject(
          rampUsers(10) during (20.seconds)
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}