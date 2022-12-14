package de.htwg
package controller.controllerComponent

import controller.{MovePossible, PlayerState, PutCommand}
import model.{Field, Move, Stone}
import util.{Event, Observable, UndoManager}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

trait ControllerInterface() extends Observable {
  val playerState: PlayerState
  val movePossible: MovePossible
  def doAndPublish(doThis: Move => Field, move: Move): Unit
  def doAndPublish(doThis: => Field): Unit
  def put(move: Move): Field
  def undo: Field
  def redo: Field
  def winner(field: Field): String
  def field: Field
  override def toString: String
}
