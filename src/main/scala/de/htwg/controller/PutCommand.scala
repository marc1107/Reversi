package de.htwg
package controller

import model.Field
import model.Move
import model.Stone
import util.Command
import util.UndoManager

class PutCommand(move: Move, var field: Field) extends Command:
  override def doStep(field: Field): Field = field.put(move.stone, move.r, move.c)
  override def undoStep(field: Field): Field =
    val fieldTemp: Field = this.field
    this.field = field
    fieldTemp
  override def redoStep(field: Field): Field =
    val fieldTemp: Field = this.field
    this.field = field
    fieldTemp
