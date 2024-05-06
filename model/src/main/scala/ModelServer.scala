import CoreModule.field
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.ModelApi
import fieldComponent.FieldInterface

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object ModelServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mySystem")
    implicit val executionContext: ExecutionContext = system.dispatcher
    val host = "0.0.0.0"
    val port = 8080

    val gameField: FieldInterface = field

    val fieldApi = new ModelApi(gameField)
    val routes: Route = fieldApi.routes

    // Start the server
    val bindingFuture = Http().newServerAt(host, port).bind(routes)

    println(s"Server online at http://$host:$port/")
    while(true) {
    }
    /*println(s"Server online at http://0.0.0.0:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())*/
  }
}