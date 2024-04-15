package controllerComponent

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import fieldComponent.{FieldInterface, Move, Stone, Field}
import lib.PutCommand

class PutCommandSpec extends AnyWordSpec {
  var field = new Field(1, Stone.Empty)
  val putCommand = new PutCommand(Move(Stone.B, 1, 1), field)
  "The PutCommand" should {
    "have a do step" in {
      field = putCommand.doStep(field).asInstanceOf[Field]
      field.get(1, 1) should be(Stone.B)
    }
    "have a undo step" in {
      field = putCommand.undoStep(field).asInstanceOf[Field]
      field.get(1, 1) should be(Stone.Empty)
    }
    "have a redo step" in {
      field = putCommand.redoStep(field).asInstanceOf[Field]
      field.get(1, 1) should be(Stone.B)
    }
  }
}
