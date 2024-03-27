package de.htwg
package model.fileIoComponent.fileIoJsonImpl

import de.htwg.controller.PlayerState
import de.htwg.model.Stone
import de.htwg.model.fieldComponent.{Field, FieldInterface}
import de.htwg.model.fileIoComponent.FileIOInterface
import play.api.libs.json.*

import scala.io.Source

class FileIO extends FileIOInterface {

  override def load: (FieldInterface, PlayerState) = {
    val source: String = Source.fromFile("field.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val size = (json \ "field" \ "size").get.toString.toInt
    val playerState = (json \ "field" \ "playerState").get.toString
    var player: PlayerState = new PlayerState

    if (playerState != player.getStone.toString) {
      player.changeState
    }

    var field: FieldInterface = new Field(size, Stone.Empty)

    for (index <- 0 until size * size) {
      val row = (json \\ "row")(index).as[Int]
      val col = (json \\ "col")(index).as[Int]
      val value = (json \\ "cell")(index).as[String]
      value match {
        case " " => field = field.put(Stone.Empty, row, col)
        case "□" => field = field.put(Stone.W, row, col)
        case "■" => field = field.put(Stone.B, row, col)
        case _ =>
      }
    }
    (field, player)
  }

  override def save(field: FieldInterface, player: PlayerState): Unit = {
    import java.io.*
    val pw = new PrintWriter(new File("field.json"))
    pw.write(Json.prettyPrint(fieldToJson(field, player)))
    pw.close
  }

  def fieldToJson(field: FieldInterface, player: PlayerState) = {
    Json.obj(
      "field" -> Json.obj(
        "size" -> JsNumber(field.size),
        "playerState" -> player.getStone.toString,
        "cells" -> Json.toJson(
          for {
            row <- 1 until field.size + 1
            col <- 1 until field.size + 1
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
