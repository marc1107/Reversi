package controllerComponent

import fieldComponent.{FieldInterface, Move, Stone}
import fileIoComponent.FileIOInterface
import lib.{Event, MovePossible, Observable, PutCommand, UndoManager}
import playerStateComponent.PlayerState

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

class Controller(using var fieldC: FieldInterface, val fileIo: FileIOInterface) extends ControllerInterface() with Observable:
  private val undoManager = new UndoManager
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

  def doAndPublish(doThis: => FieldInterface): Unit =
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
    fileIo.save(fieldC, this.playerState)
    fieldC

  def load: FieldInterface =
    val tupel = fileIo.load
    fieldC = tupel(0)
    if (this.playerState.getStone.toString != tupel(1).getStone.toString) {
      this.playerState.changeState
    }
    fieldC

  def field: FieldInterface = fieldC

  def winner(field: FieldInterface): String = Stone.B.toString


  override def toString: String = fieldC.toString
