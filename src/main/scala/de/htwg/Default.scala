package de.htwg

import de.htwg.controller.controllerComponent.{Controller, ControllerInterface}
import de.htwg.model.Stone
import de.htwg.model.fieldComponent.{Field, FieldInterface}
import de.htwg.model.fileIoComponent.FileIOInterface
import de.htwg.model.fileIoComponent.fileIoXmlImpl.FileIO

object Default {
  var field = new Field(8, Stone.Empty)
  field = field.put(Stone.W, 4, 4)
  field = field.put(Stone.B, 4, 5)
  field = field.put(Stone.B, 5, 4)
  field = field.put(Stone.W, 5, 5)

  given FieldInterface = field

  given ControllerInterface = Controller()

  given FileIOInterface = FileIO()
}
