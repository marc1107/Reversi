package de.htwg
package model.fileIoComponent.fileIoXmlImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import model.fieldComponent.FieldInterface
import model.fieldComponent.Field
import de.htwg.model.Stone
import de.htwg.controller.PlayerState
import model.fileIoComponent.FileIOInterface

import scala.xml.{NodeSeq, PrettyPrinter}

class FileIO extends FileIOInterface {

  override def load: (FieldInterface, PlayerState) = {
    val file = scala.xml.XML.loadFile("field.xml")
    val sizeAttr = (file \\ "field" \ "@size")
    val playerState = (file \\ "field" \ "@playerState").toString()
    val size = sizeAttr.text.toInt
    var field: FieldInterface = new Field(size, Stone.Empty)
    var player: PlayerState = new PlayerState

    if (playerState != player.getStone.toString) {
      player.changeState
    }


    val cellNodes = (file \\ "cell")
    for (cell <- cellNodes) {
      val row: Int = (cell \ "@row").text.toInt
      val col: Int = (cell \ "@col").text.toInt
      val value: String = cell.text.trim.toString
      value match {
        case " " => field = field.put(Stone.Empty, row, col)
        case "□" => field = field.put(Stone.W, row, col)
        case "■" => field = field.put(Stone.B, row, col)
        case _ =>
      }
    }
    (field, player)
  }

  def save(field: FieldInterface, player: PlayerState): Unit =
    saveString(field, player)

  def saveXML(field: FieldInterface, player: PlayerState): Unit = {
    scala.xml.XML.save("field.xml", fieldToXml(field, player))
  }

  def saveString(field: FieldInterface, player: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("field.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(fieldToXml(field, player))
    pw.write(xml)
    pw.close
  }
  def fieldToXml(field: FieldInterface, player: PlayerState) = {
    <field size={ field.size.toString } playerState={ player.getStone.toString }>
      {
      for {
        row <- 1 until field.size + 1
        col <- 1 until field.size + 1
      } yield cellToXml(field, row, col)
      }
    </field>
  }

  def cellToXml(field: FieldInterface, row: Int, col: Int) = {
    <cell row={ row.toString } col={ col.toString }>
    { field.get(row, col).toString }
    </cell>
  }
}
