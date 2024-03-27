package de.htwg.controller

import de.htwg.model.Stone

class PlayerState {
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
