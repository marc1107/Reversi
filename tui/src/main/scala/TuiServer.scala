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
    val host = "0.0.0.0"

    val tui = new TUI

    val tuiApi = new TuiApi(tui)
    val routes: Route = tuiApi.routes

    addAsObserver(s"http://$host:$port/tui/update")

    // Start the server
    val bindingFuture = Http().newServerAt(host, port).bind(routes)

    println(s"Server online at http://$host:$port/")
    /*while(true) {
    }*/
  }

  private def addAsObserver(myUrl: String): Unit = {
    val url = new URL("http://0.0.0.0:8082/core/addObserver") // replace with your API URL
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