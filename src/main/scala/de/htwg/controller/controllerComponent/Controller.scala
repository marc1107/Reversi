package de.htwg
package controller.controllerComponent

import controller.{MovePossible, PlayerState, PutCommand}
import de.htwg.model.fieldComponent.FieldInterface
import model.{Move, Stone}
import util.{Event, Observable, UndoManager}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class Controller(var fieldC: FieldInterface) extends ControllerInterface() with Observable :
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

  def field: FieldInterface = fieldC

  /*def winner(field: Field, b: Int = 0, w: Int = 0): String =
    var countB = b
    var countW = w
    for (i <- 1 to field.size; j <- 1 to field.size) {
      field.get(i, j) match {
        case Stone.B => countB += 1
        case Stone.W => countW += 1
      }
    }
    countW.compareTo(countB) match {
      case -1 => Stone.B.toString
      case 1 => Stone.W.toString
      case _ => "Unentschieden"
    }*/
  def winner(field: FieldInterface): String = Stone.B.toString


  override def toString: String = fieldC.toString

  /**
   * Strategy Pattern to check if a Move is possible
   */
  /*object MovePossible {
    var strat = 1
    var strategy: Move => Try[ListBuffer[Move]] = if (strat == 0) strategy1 else strategy2

    def strategy1(move: Move): Try[ListBuffer[Move]] =
      field.get(move.r, move.c) == Stone.Empty
      val lb = new ListBuffer[Move]
      lb.append(move)
      Success(lb)


    def strategy2(move: Move): Try[ListBuffer[Move]] =
      def isInsideField(r: Int, c: Int): Boolean = {
        r >= 1 && r <= field.size && c >= 1 && c <= field.size
      }

      def outflankedInDir(row: Int, col: Int, cDelta: Int, rDelta: Int): ListBuffer[Move] = {
        val outflanked = ListBuffer[Move]()
        var r: Int = row + rDelta
        var c: Int = col + cDelta

        while (isInsideField(r, c) && field.get(r, c) != Stone.Empty) {
          // wenn gegnerischer Stein
          if (field.get(r, c) != playerState.getStone)
            outflanked += Move(playerState.getStone, r, c)
            r += rDelta
            c += cDelta
          else
            return outflanked
        }
        new ListBuffer[Move]
      }

      def outFlanked(r: Int, c: Int): ListBuffer[Move] = {
        val outflanked = ListBuffer[Move]()

        for (rDelta <- -1 to 1; cDelta <- -1 to 1) {
          if (rDelta != 0 || cDelta != 0)
            outflanked.appendAll(outflankedInDir(r, c, cDelta, rDelta))
        }

        outflanked
      }

      field.get(move.r, move.c) match {
        case Stone.Empty =>
          val outflanked = outFlanked(move.r, move.c)
          outflanked.isEmpty match
            case false => Success(outflanked)
            case _ => Failure(new Exception("Nothing to turn"))
        case _ => Failure(new Exception("Cell not empty"))
      }
  }*/

  /**
   * state pattern contains whose players turn it is
   */
  /*object playerState {
    var state: Int = player1

    def getStone: Stone = {
      state match {
        case 1 => Stone.B
        case 2 => Stone.W
      }
    }

    def changeState = {
      state match {
        case 1  => state = player2
        case 2 => state = player1
      }
      state
    }

    def player1: Int = 1

    def player2: Int = 2
  }*/
