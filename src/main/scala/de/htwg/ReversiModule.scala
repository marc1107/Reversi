package de.htwg

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import controller.controllerComponent.*
import model.Stone
import model.fieldComponent.{Field, FieldInterface}
import model.matrixComponent.MatrixInterface
import model.matrixComponent.Matrix

class ReversiModule extends AbstractModule {
  val defaultSize:Int = 8
  val defaultStone:Stone = Stone.Empty

  override def configure() = {
    var field = new Field(defaultSize, Stone.Empty)
    field = field.put(Stone.W, 4, 4)
    field = field.put(Stone.B, 4, 5)
    field = field.put(Stone.B, 5, 4)
    field = field.put(Stone.W, 5, 5)

    bind(classOf[ControllerInterface]).toInstance(new Controller(field))
    bind(classOf[FieldInterface]).toInstance(new Field(defaultSize, defaultStone))
    bind(classOf[MatrixInterface[Stone]]).toInstance(new Matrix(defaultSize, defaultStone))
  }
}
