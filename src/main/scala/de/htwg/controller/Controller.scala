package de.htwg
package controller

import model.Field
import model.Stone
import model.Move
import util.Observable
import util.UndoManager

import scala.collection.mutable.ListBuffer

case class Controller(var field: Field) extends Observable:
  val undoManager = new UndoManager
  def doAndPublish(doThis: Move => Field, move: Move): Unit =
    val list = MovePossible.strategy(move)
    if (list.nonEmpty)
      playerState.changeState
      field = doThis(move)
      list.foreach(el => field = field.put(el.stone, el.r, el.c))

    notifyObservers

  def doAndPublish(doThis: => Field) =
    field = doThis
    notifyObservers

  def put(move: Move): Field = undoManager.doStep(field, PutCommand(move))
  def undo: Field = undoManager.undoStep(field)
  def redo: Field = undoManager.redoStep(field)


  override def toString: String = field.toString

  /**
   * Strategy Pattern to check if a Move is possible
   */
  object MovePossible {
    var strat = 1
    var strategy: Move => ListBuffer[Move] = if (strat == 0) strategy1 else strategy2

    def strategy1(move: Move): ListBuffer[Move] =
      field.get(move.r, move.c) == Stone.Empty
      val lb = new ListBuffer[Move]
      lb.append(move)
      lb


    def strategy2(move: Move): ListBuffer[Move] =
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

      def isMoveLegal(r: Int, c: Int): Boolean = {
        outFlanked(r, c).nonEmpty
      }

      if (field.get(move.r, move.c) == Stone.Empty)
        outFlanked(move.r, move.c)
      else
        new ListBuffer[Move]
      //isMoveLegal(move.r, move.c)
  }

  /**
   * state pattern contains whose players turn it is
   */
  object playerState {
    var state: Int = player1

    def getStone: Stone = {
      state match {
        case 1 => Stone.B
        case 2 => Stone.W
      }
    }

    def changeState = {
      state match {
        case 1 => state = player2
        case 2 => state = player1
      }
      state
    }

    def player1: Int = 1
    def player2: Int = 2
  }