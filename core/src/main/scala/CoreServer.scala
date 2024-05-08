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
    val host = "core-service"

    val controller: ControllerInterface = CoreModule.controller

    val coreApi = new CoreApi(controller)
    val routes: Route = coreApi.routes

    // Start the server
    val bindingFuture = Http().newServerAt(host, port).bind(routes)

    println(s"Server online at http://$host:$port/")
    while(true) {
    }
    /*println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())*/
  }
}