package de.htwg.controller
package modules

import controllerComponent.ControllerInterface
import controllerComponent.Controller
import de.htwg.model.Stone
import de.htwg.model.fieldComponent.{Field, FieldInterface}

object Default {
  var field = new Field(8, Stone.Empty)
  field = field.put(Stone.W, 4, 4)
  field = field.put(Stone.B, 4, 5)
  field = field.put(Stone.B, 5, 4)
  field = field.put(Stone.W, 5, 5)

  given ControllerInterface = Controller(field)
}
