package lib

import fieldComponent.{Field, FieldInterface, Stone}
import lib.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MockCommand extends Command {
  var doStepCalled = false
  var undoStepCalled = false
  var redoStepCalled = false

  override def doStep(field: FieldInterface): FieldInterface = {
    doStepCalled = true
    field
  }

  override def undoStep(field: FieldInterface): FieldInterface = {
    undoStepCalled = true
    field
  }

  override def redoStep(field: FieldInterface): FieldInterface = {
    redoStepCalled = true
    field
  }
}

class UndoManagerSpec extends AnyWordSpec with Matchers {
  "An UndoManager" should {
    "call the doStep method of the Command when doStep is called" in {
      val command = new MockCommand
      val undoManager = new UndoManager
      val field = new Field(3, Stone.Empty)

      undoManager.doStep(field, command)
      assert(command.doStepCalled)
    }

    "call the undoStep method of the Command when undoStep is called" in {
      val command = new MockCommand
      val undoManager = new UndoManager
      val field = new Field(3, Stone.Empty)

      undoManager.doStep(field, command)
      undoManager.undoStep(field)
      assert(command.undoStepCalled)
    }

    "call the redoStep method of the Command when redoStep is called" in {
      val command = new MockCommand
      val undoManager = new UndoManager
      val field = new Field(3, Stone.Empty)

      undoManager.doStep(field, command)
      undoManager.undoStep(field)
      undoManager.redoStep(field)
      assert(command.redoStepCalled)
    }
  }
}