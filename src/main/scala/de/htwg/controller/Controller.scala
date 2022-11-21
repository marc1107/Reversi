package de.htwg
package controller

import model.Field
import model.Stone
import model.Move
import util.Observable

case class Controller(var field: Field) extends Observable:
  def doAndPublish(doThis: Move => Field, move: Move): Unit =
    field = doThis(move)
    notifyObservers

  def put(move: Move): Field =
    if (MovePossible.strategy(move))
      field.put(move.stone, move.x, move.y)
    else
      field

  override def toString = field.toString

  def isMovePossible(move: Move): Boolean = MovePossible.strategy(move)

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
      val stone: Stone = field.get(move.x, move.y)
      stone.toString == " "
  }