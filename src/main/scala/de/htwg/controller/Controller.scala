package de.htwg
package controller

import model.Field
import model.Stone
import model.Move
import util.Observable

import scala.collection.mutable.ListBuffer

case class Controller(var field: Field) extends Observable:
  def doAndPublish(doThis: Move => Field, move: Move): Unit =
    field = doThis(move)
    notifyObservers

  def put(move: Move): Field =
    val list = MovePossible.strategy(move)
    if (list.nonEmpty)
      playerState.changeState
      field = field.put(move.stone, move.x, move.y)
      list.foreach(el => field = field.put(el.stone, el.x, el.y))
    field

  override def toString: String = field.toString

  /**
   * Strategy Pattern to check if a Move is possible
   */
  object MovePossible {
    val strat = 1
    var strategy: Move => ListBuffer[Move] = if (strat == 0) strategy1 else strategy2

    def strategy1(move: Move): ListBuffer[Move] =
      // TODO: implement a strategy
      field.get(move.x, move.y) == Stone.Empty
      new ListBuffer[Move]

    def strategy2(move: Move): ListBuffer[Move] =
      // TODO: implement a strategy
      def isInsideField(r: Int, c: Int): Boolean = {
        r >= 1 && r <= field.size && c >= 1 && c <= field.size
      }

      def outflankedInDir(x: Int, y: Int, rDelta: Int, cDelta: Int): ListBuffer[Move] = {
        val outflanked = ListBuffer[Move]()
        var r: Int = y + rDelta
        var c: Int = x + cDelta

        while (isInsideField(r, c) && field.get(c, r) != Stone.Empty) {
          // wenn gegnerischer Stein
          if (field.get(c, r) != playerState.getStone)
            outflanked += Move(playerState.getStone, c, r)
            r += rDelta
            c += cDelta
          else
            return outflanked
        }
        new ListBuffer[Move]
      }

      def outFlanked(x: Int, y: Int): ListBuffer[Move] = {
        val outflanked = ListBuffer[Move]()

        for (rDelta <- -1 to 1) {
          for (cDelta <- -1 to 1) {
            if (rDelta != 0 || cDelta != 0)
              outflanked.appendAll(outflankedInDir(x, y, rDelta, cDelta))
          }
        }

        outflanked
      }

      def isMoveLegal(x: Int, y: Int): Boolean = {
        outFlanked(x, y).nonEmpty
      }

      if (field.get(move.x, move.y) == Stone.Empty)
        outFlanked(move.x, move.y)
      else
        new ListBuffer[Move]
      //isMoveLegal(move.x, move.y)
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