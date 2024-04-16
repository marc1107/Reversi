package fileIoComponent.fileIoJsonImpl

import fileIoComponent.{FileIOInterface, PlayerState}
import fieldComponent.{Field, FieldInterface, Stone}
import play.api.libs.json.*

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

  override def save(field: FieldInterface, player: PlayerState): Unit =
    saveString(field, player)

  def saveString(field: FieldInterface, player: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("field.json"))
    pw.write(Json.prettyPrint(fieldToJson(field, player)))
    pw.close()
  }

  private def createEmptyField(size: Int): FieldInterface = new Field(size, Stone.Empty)

  private def fieldToJson(field: FieldInterface, player: PlayerState) = {
    Json.obj(
      "field" -> Json.obj(
        "size" -> JsNumber(field.size),
        "playerState" -> player.getStone.toString,
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
}
