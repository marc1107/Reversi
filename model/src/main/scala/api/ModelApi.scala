package api

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}
import playerStateComponent.PlayerState

class ModelApi(var field: FieldInterface) {
  val log: Logger = LoggerFactory.getLogger(getClass)
  val playerState: PlayerState = new PlayerState

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

          field = field.put(stone, row, col)

          complete(field.toJsObject.toString())
        }
      }
    } ~
    path("size") {
      get {
        log.info("Received GET request for field size")
        complete(Json.obj("size" -> field.size).toString())
      }
    } ~
    path("getStone") {
      get {
        parameters("row".as[Int], "col".as[Int]) { (row, col) =>
          val stone: Stone = field.get(row, col)
          complete(Json.obj("stone" -> stone.toString).toString())
        }
      }
    } ~
    path("playerState") {
      get {
        log.info("Received GET request stone of playerState")
        complete(Json.obj("playerStone" -> playerState.getStone.toString).toString())
      }
    } ~
    path("changePlayerState") {
      get {
        log.info("Received POST request for change player state")
        val state: Int = playerState.changeState
        log.info(s"Changed player state to $state")
        complete(Json.obj("playersTurn" -> state).toString())
      }
    }
  }
}