import tuiComponent.TUI
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.TuiApi
import play.api.libs.json.Json

import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}
import scala.concurrent.ExecutionContext
import scala.io.StdIn

object TuiServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("mySystem")
    implicit val executionContext: ExecutionContext = system.dispatcher
    val port = 8084

    val tui = new TUI

    val tuiApi = new TuiApi(tui)
    val routes: Route = tuiApi.routes

    addAsObserver(s"http://localhost:$port/tui/update")

    // Start the server
    val bindingFuture = Http().newServerAt("localhost", port).bind(routes)

    tui.run()

    println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  private def addAsObserver(myUrl: String): Unit = {
    val url = new URL("http://localhost:8082/core/addObserver") // replace with your API URL
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")

    val json: String = Json.obj(
      "url" -> myUrl
    ).toString()

    val outputStreamWriter = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
    outputStreamWriter.write(json)
    outputStreamWriter.close()

    if (connection.getResponseCode != HttpURLConnection.HTTP_OK) {
      throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode)
    }
  }
}