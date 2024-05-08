package fileIoComponent.fileIoJsonImpl

import fileIoComponent.FileIOInterface
import fieldComponent.{Field, FieldInterface, Stone}
import play.api.libs.json.*
import playerStateComponent.PlayerState

import java.io.{File, PrintWriter}
import scala.util.Using
import scala.io.Source

class FileIO extends FileIOInterface {

  override def load: (FieldInterface, PlayerState) = {
    val source: String = Using(Source.fromFile("field.json")) { source =>
      source.getLines.mkString
    }.get
    val json: JsValue = Json.parse(source)
    val size = (json \ "field" \ "size").get.toString.toInt
    val playerState = (json \ "field" \ "playerState").get.toString

    val player: PlayerState = new PlayerState

    if (playerState != player.getStone.toString) {
      player.changeState
    }

    val field = (json \ "field" \ "cells").as[JsArray].value.foldLeft(createEmptyField(size)) { (accField, cell) =>
      val row = (cell \ "row").as[Int]
      val col = (cell \ "col").as[Int]
      val value = (cell \ "cell").as[String]
      val stone = value match {
        case "□" => Stone.W
        case "■" => Stone.B
        case _ => Stone.Empty
      }
      accField.put(stone, row, col)
    }
    (field, player)
  }

  override def save(field: FieldInterface): Unit =
    saveString(field)

  def saveString(field: FieldInterface): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("field.json"))
    pw.write(Json.prettyPrint(fieldToJson(field)))
    pw.close()
  }

  def createEmptyField(size: Int): FieldInterface = new Field(size, Stone.Empty)

  def fieldToJson(field: FieldInterface): JsObject = {
    Json.obj(
      "field" -> Json.obj(
        "size" -> JsNumber(field.size),
        "playerState" -> getPlayerStateFromApi.toString,
        "cells" -> Json.toJson(
          for {
            row <- 1 to field.size
            col <- 1 to field.size
          } yield {
            Json.obj(
              "row" -> row,
              "col" -> col,
              "cell" -> field.get(row, col).toString
            )
          }
        )
      )
    )
  }

  def getPlayerStateFromApi: Stone = {
    val url = "http://model-service:8080/field/playerState" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val playerStone: String = (json \ "playerStone").as[String]

    playerStone match {
      case "□" => Stone.W
      case "■" => Stone.B
      case _ => Stone.Empty
    }
  }
}
