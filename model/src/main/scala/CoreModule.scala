import fieldComponent.{Field, FieldInterface, Stone}

object CoreModule {
  private val baseField = new Field(8, Stone.Empty)
  private val field1: Field = baseField.put(Stone.W, 4, 4)
  private val field2: Field = field1.put(Stone.B, 4, 5)
  private val field3: Field = field2.put(Stone.B, 5, 4)
  val field: Field = field3.put(Stone.W, 5, 5)

  given FieldInterface = field
}