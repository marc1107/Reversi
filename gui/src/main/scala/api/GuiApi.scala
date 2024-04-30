package api

import GuiComponent.GUI
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import lib.Event
import org.slf4j.{Logger, LoggerFactory}

class GuiApi(val gui: GUI) {
  val log: Logger = LoggerFactory.getLogger(getClass)

  val routes: Route = pathPrefix("gui") {
    path("update") {
      get {
        parameter("event") {
          case "quit" =>
            gui.update(Event.Quit)
            complete(StatusCodes.OK)
          case "move" =>
            gui.update(Event.Move)
            complete(StatusCodes.OK)
          case "end" =>
            gui.update(Event.End)
            complete(StatusCodes.OK)
          case _ =>
            complete(StatusCodes.BadRequest, "Invalid event")
        }
      }
    }
  }
}