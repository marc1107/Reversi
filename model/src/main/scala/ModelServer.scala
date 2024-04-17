import CoreModule.field
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.ModuleApi
import fieldComponent.FieldInterface

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object ModelServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mySystem")
    implicit val executionContext: ExecutionContext = system.dispatcher

    val gameField: FieldInterface = field

    val fieldApi = new ModuleApi(gameField)
    val routes: Route = fieldApi.routes

    // Start the server
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}