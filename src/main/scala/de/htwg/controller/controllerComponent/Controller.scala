package de.htwg
package controller.controllerComponent

import controller.{MovePossible, PlayerState, PutCommand}
import model.fieldComponent.FieldInterface
import Default.given
import model.{Move, Stone}
import util.{Event, Observable, UndoManager}
import com.google.inject.name.Named
import de.htwg.model.fileIoComponent.fileIoJsonImpl.FileIO

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class Controller(using var fieldC: FieldInterface) extends ControllerInterface() with Observable :
  val undoManager = new UndoManager
  val movePossible: MovePossible = new MovePossible(this)
  val playerState: PlayerState = new PlayerState
  val fileIo = new FileIO

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

  def winner(field: FieldInterface): String = Stone.B.toString


  override def toString: String = fieldC.toString
