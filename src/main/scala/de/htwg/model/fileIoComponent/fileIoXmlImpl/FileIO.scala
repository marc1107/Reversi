package de.htwg
package model.fileIoComponent.fileIoXmlImpl

import de.htwg.controller.PlayerState
import de.htwg.model.Stone
import de.htwg.model.fieldComponent.{Field, FieldInterface}
import de.htwg.model.fileIoComponent.FileIOInterface

import scala.xml.{NodeSeq, PrettyPrinter}

class FileIO extends FileIOInterface {

  override def load: (FieldInterface, PlayerState) = {
    val file = scala.xml.XML.loadFile("field.xml")
    val sizeAttr = file \\ "field" \ "@size"
    val playerState = (file \\ "field" \ "@playerState").toString()
    val size = sizeAttr.text.toInt
    val player: PlayerState = new PlayerState

    if (playerState != player.getStone.toString) {
      player.changeState
    }


    val field = (file \\ "cell").foldLeft(createEmptyField(size)) { (accField, cell) =>
      val row = (cell \ "@row").text.toInt
      val col = (cell \ "@col").text.toInt
      val value = cell.text.trim
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

  def saveXML(field: FieldInterface, player: PlayerState): Unit = {
    scala.xml.XML.save("field.xml", fieldToXml(field, player))
  }

  private def saveString(field: FieldInterface, player: PlayerState): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("field.xml"))
    val prettyPrinter = new scala.xml.PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(fieldToXml(field, player))
    pw.write(xml)
    pw.close()
  }

  private def createEmptyField(size: Int): FieldInterface = new Field(size, Stone.Empty)

  private def fieldToXml(field: FieldInterface, player: PlayerState) = {
    <field size={field.size.toString} playerState={player.getStone.toString}>
      {for {
      row <- 1 to field.size
      col <- 1 to field.size
    } yield cellToXml(field, row, col)}
    </field>
  }

  private def cellToXml(field: FieldInterface, row: Int, col: Int) = {
    <cell row={row.toString} col={col.toString}>
      {field.get(row, col).toString}
    </cell>
  }
}