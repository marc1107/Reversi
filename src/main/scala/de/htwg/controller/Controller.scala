package de.htwg
package controller

import model.Field
import model.Stone
import util.Observable

case class Controller(var field: Field) extends Observable:
  def put(stone: Stone, x: Int, y: Int): Unit =
    field = field.put(stone, x, y)
    notifyObservers
  override def toString: String = field.toString