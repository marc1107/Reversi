package de.htwg.controller

import de.htwg.model.Stone

class PlayerState {
  private var state: Int = player1

  def getStone: Stone = {
    state match {
      case 1 => Stone.B
      case 2 => Stone.W
    }
  }

  def changeState: Int = {
    state match {
      case 1 => state = player2
      case 2 => state = player1
    }
    state
  }

  private def player1: Int = 1

  private def player2: Int = 2
}
