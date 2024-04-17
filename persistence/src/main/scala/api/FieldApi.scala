package api

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import play.api.libs.json.{JsValue, Json}

class FieldApi(var field: FieldInterface) {
  val routes: Route = pathPrefix("field") {
    pathEnd {
      get {
        complete(field.toJsObject.toString)
      } ~
      post {
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
        complete(field.size.toString)
      }
    } ~
    path("getStone") {
      post {
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