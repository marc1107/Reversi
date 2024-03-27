package de.htwg

import de.htwg.controller.controllerComponent.{Controller, ControllerInterface}
import de.htwg.model.Stone
import de.htwg.model.fieldComponent.{Field, FieldInterface}
import de.htwg.model.fileIoComponent.FileIOInterface
import de.htwg.model.fileIoComponent.fileIoXmlImpl.FileIO

object Default {
  private val baseField = new Field(8, Stone.Empty)
  val field1: Field = baseField.put(Stone.W, 4, 4)
  val field2: Field = field1.put(Stone.B, 4, 5)
  val field3: Field = field2.put(Stone.B, 5, 4)
  val field: Field = field3.put(Stone.W, 5, 5)

  given FieldInterface = field

  given ControllerInterface = Controller()

  given FileIOInterface = FileIO()
}
