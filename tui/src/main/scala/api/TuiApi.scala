package api

import tuiComponent.TUI
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import lib.Event
import org.slf4j.{Logger, LoggerFactory}

class TuiApi(val tui: TUI) {
  val log: Logger = LoggerFactory.getLogger(getClass)

  val routes: Route = pathPrefix("tui") {
    path("update") {
      get {
        parameter("event") {
          case "quit" =>
            tui.update(Event.Quit)
            complete(StatusCodes.OK)
          case "move" =>
            tui.update(Event.Move)
            complete(StatusCodes.OK)
          case "end" =>
            tui.update(Event.End)
            complete(StatusCodes.OK)
          case _ =>
            complete(StatusCodes.BadRequest, "Invalid event")
        }
      }
    }
  }
}