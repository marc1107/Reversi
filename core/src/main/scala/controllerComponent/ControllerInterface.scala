package controllerComponent

import fieldComponent.{FieldInterface, Move}
import lib.{MovePossible, Observable}
import playerStateComponent.PlayerState

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

def field: FieldInterface

override def toString: String
}
