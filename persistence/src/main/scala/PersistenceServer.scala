import CoreModule.field
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.{ModelApi, PersistenceApi}
import fieldComponent.FieldInterface
import fileIoComponent.FileIOInterface

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object PersistenceServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mySystem")
    implicit val executionContext: ExecutionContext = system.dispatcher
    val port = 8081
    val host = "persitence-service"

    val gameField: FieldInterface = field
    val fileIO: FileIOInterface = new fileIoComponent.fileIoJsonImpl.FileIO

    val fieldApi = new PersistenceApi(gameField,fileIO)
    val routes: Route = fieldApi.routes

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