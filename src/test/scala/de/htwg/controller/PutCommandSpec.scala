package de.htwg
package controller

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import model.Field
import model.Stone
import model.Move

class PutCommandSpec extends AnyWordSpec {
  var field = new Field(1, Stone.Empty)
  val putCommand = new PutCommand(Move(Stone.B, 1, 1), field)
  "The PutCommand" should {
    "have a do step" in {
      field = putCommand.doStep(field)
      field.get(1, 1) should be (Stone.B)
    }
    "have a undo step" in {
      field = putCommand.undoStep(field)
      field.get(1, 1) should be (Stone.Empty)
    }
    "have a redo step" in {
      field = putCommand.redoStep(field)
      field.get(1, 1) should be (Stone.B)
    }
  }
}
