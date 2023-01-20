package de.htwg
package controller.controllerComponent

import controller.{MovePossible, PlayerState, PutCommand}
import model.fieldComponent.FieldInterface
import Default.given
import model.{Move, Stone}
import util.{Event, Observable, UndoManager}
import com.google.inject.name.Named
import de.htwg.model.fileIoComponent.FileIOInterface
import de.htwg.model.fileIoComponent.fileIoJsonImpl.FileIO

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class Controller(using var fieldC: FieldInterface, val fileIo: FileIOInterface) extends ControllerInterface() with Observable :
  val undoManager = new UndoManager
  val movePossible: MovePossible = new MovePossible(this)
  val playerState: PlayerState = new PlayerState

  def doAndPublish(doThis: Move => FieldInterface, move: Move): Unit =
    val t = movePossible.strategy(move) // returns a Try
    t match
      case Success(list) =>
        playerState.changeState
        fieldC = doThis(move)
        fieldC = fieldC.put(move.stone, move.r, move.c)
        list.foreach(el => fieldC = fieldC.put(el.stone, el.r, el.c))
      case Failure(f) => println(f.getMessage)

    notifyObservers(Event.Move)

  def doAndPublish(doThis: => FieldInterface) =
    fieldC = doThis
    notifyObservers(Event.Move)

  def put(move: Move): FieldInterface =
    undoManager.doStep(fieldC, PutCommand(move, fieldC))

  def undo: FieldInterface =
    playerState.changeState
    undoManager.undoStep(fieldC)

  def redo: FieldInterface =
    playerState.changeState
    undoManager.redoStep(fieldC)

  def save: FieldInterface =
    fileIo.save(fieldC)
    fieldC

  def load: FieldInterface =
    fieldC = fileIo.load
    fieldC

  def field: FieldInterface = fieldC

  def winner(field: FieldInterface): String =
    if (countStone(Stone.B) > countStone(Stone.W))
      Stone.B.toString
    else if (countStone(Stone.B) < countStone(Stone.W))
      Stone.W.toString
    else
      "keiner"

  def countStone(stone: Stone): Int =
    var counter: Int = 0
    for (i <- 1 to fieldC.size; j <- 1 to fieldC.size) {
      if (fieldC.get(i, j).equals(stone))
        counter += 1
    }
    counter

  override def toString: String = fieldC.toString
