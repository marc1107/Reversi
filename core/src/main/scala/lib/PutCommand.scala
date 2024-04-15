package lib

import fieldComponent.{FieldInterface, Move}

class PutCommand(move: Move, var field: FieldInterface) extends Command:
  override def doStep(field: FieldInterface): FieldInterface = field.put(move.stone, move.r, move.c)

  override def undoStep(field: FieldInterface): FieldInterface =
    val fieldTemp: FieldInterface = this.field
    this.field = field
    fieldTemp

  override def redoStep(field: FieldInterface): FieldInterface =
    val fieldTemp: FieldInterface = this.field
    this.field = field
    fieldTemp
