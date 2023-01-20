package de.htwg
package controller.controllerComponent

import controller.{MovePossible, PlayerState, PutCommand}
import model.fieldComponent.FieldInterface
import model.{Move, Stone}
import util.{Event, Observable, UndoManager}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

trait ControllerInterface extends Observable {
  val playerState: PlayerState
  val movePossible: MovePossible
  def doAndPublish(doThis: Move => FieldInterface, move: Move): Unit
  def doAndPublish(doThis: => FieldInterface): Unit
  def put(move: Move): FieldInterface
  def undo: FieldInterface
  def redo: FieldInterface
  def save: FieldInterface
  def load: FieldInterface
  def winner(field: FieldInterface): String
  def countStone(stone: Stone): Int
  def field: FieldInterface
  override def toString: String
}
