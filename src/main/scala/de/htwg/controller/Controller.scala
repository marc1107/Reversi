package de.htwg
package controller

import model.Field
import model.Stone
import model.Move
import util.Observable

import scala.collection.mutable.ListBuffer

case class Controller(var field: Field) extends Observable:
  def doAndPublish(doThis: Move => Field, move: Move): Unit =
    val list = MovePossible.strategy(move)
    if (list.nonEmpty)
      playerState.changeState
      field = doThis(move)
      list.foreach(el => field = field.put(el.stone, el.r, el.c))

    notifyObservers

  def put(move: Move): Field =
      field.put(move.stone, move.r, move.c)


  override def toString: String = field.toString

  /**
   * Strategy Pattern to check if a Move is possible
   */
  object MovePossible {
    val strat = 1
    var strategy: Move => ListBuffer[Move] = if (strat == 0) strategy1 else strategy2

    def strategy1(move: Move): ListBuffer[Move] =
      // TODO: implement a strategy
      field.get(move.r, move.c) == Stone.Empty
      new ListBuffer[Move]

    def strategy2(move: Move): ListBuffer[Move] =
      // TODO: implement a strategy
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

        for (rDelta <- -1 to 1) {
          for (cDelta <- -1 to 1) {
            if (rDelta != 0 || cDelta != 0)
              outflanked.appendAll(outflankedInDir(r, c, cDelta, rDelta))
          }
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
   * contains whose players turn it is
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