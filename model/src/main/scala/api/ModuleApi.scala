package api

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}

class ModuleApi(var field: FieldInterface) {
  val log: Logger = LoggerFactory.getLogger(getClass)

  val routes: Route = pathPrefix("field") {
    pathEnd {
      get {
        log.info("Received GET request for field")
        complete(field.toJsObject.toString)
      } ~
      post {
        log.info("Received POST request for field")
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val fieldValue: String = (jsonValue \ "field").as[JsValue].toString()
          val stoneValue: String = (jsonValue \ "stone").as[String]
          val row: Int = (jsonValue \ "row").as[Int]
          val col: Int = (jsonValue \ "col").as[Int]

          field = field.jsonToField(fieldValue)

          val stone: Stone = stoneValue match {
            case "□" => Stone.W
            case "■" => Stone.B
            case _ => Stone.Empty
          }

          field.put(stone, row, col)

          complete(field.toJsObject.toString())
        }
      }
    } ~
    path("size") {
      get {
        log.info("Received GET request for field size")
        complete(field.size.toString)
      }
    } ~
    path("getStone") {
      post {
        log.info("Received POST request for stone")
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val row: Int = (jsonValue \ "row").as[Int]
          val col: Int = (jsonValue \ "col").as[Int]
          val stone: Stone = field.get(row, col)
          complete(stone.toString)
        }
      }
    }
  }
}