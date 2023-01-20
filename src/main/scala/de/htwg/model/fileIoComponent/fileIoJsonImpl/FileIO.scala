package de.htwg
package model.fileIoComponent.fileIoJsonImpl

import de.htwg.model.Stone
import model.fieldComponent.{Field, FieldInterface}
import model.fileIoComponent.FileIOInterface
import play.api.libs.json._

import scala.io.Source

class FileIO extends FileIOInterface {

  override def load: FieldInterface = {
    val source: String = Source.fromFile("field.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val size = (json \ "field" \ "size").get.toString.toInt
    var field: FieldInterface = new Field(size, Stone.Empty)

    for (index <- 0 until size * size) {
      val row = (json \\ "row")(index).as[Int]
      val col = (json \\ "col")(index).as[Int]
      val value = (json \\ "cell")(index).as[String]
      value match {
        case " " => field = field.put(Stone.Empty, row, col)
        case "□" => field = field.put(Stone.B, row, col)
        case "■" => field = field.put(Stone.W, row, col)
        case _ =>
      }
    }
    field
  }

  override def save(field: FieldInterface): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("field.json"))
    pw.write(Json.prettyPrint(fieldToJson(field)))
    pw.close
  }

  def fieldToJson(field: FieldInterface) = {
    Json.obj(
      "field" -> Json.obj(
        "size" -> JsNumber(field.size),
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