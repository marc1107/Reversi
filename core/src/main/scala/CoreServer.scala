import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.CoreApi
import controllerComponent.ControllerInterface

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object CoreServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mySystem")
    implicit val executionContext: ExecutionContext = system.dispatcher
    val port = 8082

    val controller: ControllerInterface = CoreModule.controller

    val coreApi = new CoreApi(controller)
    val routes: Route = coreApi.routes

    // Start the server
    val bindingFuture = Http().newServerAt("localhost", port).bind(routes)

    println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}