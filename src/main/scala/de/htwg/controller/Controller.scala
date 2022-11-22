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
    if (MovePossible.strategy(move))
      playerState.changeState
      field.put(move.stone, move.x, move.y)
    else
      field

  override def toString = field.toString

  /**
   * Strategy Pattern to check if a Move is possible
   */
  object MovePossible {
    val strat = 0
    var strategy = if (strat == 0) strategy1 else strategy2

    def strategy1(move: Move): Boolean =
      // TODO: implement a strategy
      val stone: Stone = field.get(move.x, move.y)
      stone.toString == " "

    def strategy2(move: Move): Boolean =
      // TODO: implement a strategy
      def isInsideField(r: Int, c: Int): Boolean = {
        r >= 0 && r < field.size && c >= 0 && c < field.size
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
            outflanked
        }
        new ListBuffer[Move]
      }

      val stone: Stone = field.get(move.x, move.y)
      stone.toString == " "
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