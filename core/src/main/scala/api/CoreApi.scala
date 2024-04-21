package api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import controllerComponent.ControllerInterface
import fieldComponent.{Move, Stone}
import lib.UpdateObserver
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}

class CoreApi(var controller: ControllerInterface) {
  val log: Logger = LoggerFactory.getLogger(getClass)

  val routes: Route = pathPrefix("core") {
    path("doAndPublish") {
      post {
        log.info("Received POST request for doAndPublish")
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val method: String = (jsonValue \ "method").as[String]

          method match {
            case "put" =>
              log.info("Method is put")
              val stoneValue: String = (jsonValue \ "stone").as[String]
              val rowValue: Int = (jsonValue \ "row").as[Int]
              val colValue: Int = (jsonValue \ "col").as[Int]
              val stone: Stone = stoneValue match {
                case "□" => Stone.W
                case "■" => Stone.B
                case _ => Stone.Empty
              }
              val move: Move = Move(stone, rowValue, colValue)

              controller.doAndPublish(controller.put, move)

              complete(StatusCodes.OK)
            case "undo" =>
              log.info("Method is undo")
              controller.doAndPublish(controller.undo)

              complete(StatusCodes.OK)
            case "redo" =>
              log.info("Method is redo")
              controller.doAndPublish(controller.redo)

              complete(StatusCodes.OK)
            case "save" =>
              log.info("Method is save")
              controller.doAndPublish(controller.save)

              complete(StatusCodes.OK)
            case "load" =>
              log.info("Method is load")
              controller.doAndPublish(controller.load)

              complete(StatusCodes.OK)
            case _ =>
              log.error("Invalid method")
              complete(StatusCodes.BadRequest, "Invalid method")
          }
        }
      }
    } ~
    path("addObserver") {
      post {
        log.info("Received POST request for addObserver")
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val url: String = (jsonValue \ "url").as[String]
          controller.add(new UpdateObserver(url))

          complete(StatusCodes.OK)
        }
      }
    }
  }
}