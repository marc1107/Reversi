import controllerComponent.{Controller, ControllerInterface}
import fieldComponent.{Field, FieldInterface, Stone}
import fileIoComponent.FileIOInterface
import fileIoComponent.fileIoXmlImpl.FileIO

object CoreModule {
  private val baseField = new Field(8, Stone.Empty)
  val field1: Field = baseField.put(Stone.W, 4, 4)
  val field2: Field = field1.put(Stone.B, 4, 5)
  val field3: Field = field2.put(Stone.B, 5, 4)
  val field: Field = field3.put(Stone.W, 5, 5)

  given FieldInterface = field
  
  val controller: Controller = Controller()

  given ControllerInterface = controller

  given FileIOInterface = FileIO()
}