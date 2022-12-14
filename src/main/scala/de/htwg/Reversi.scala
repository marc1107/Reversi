package de.htwg

import aview.TUI
import aview.GUI
import de.htwg.controller.controllerComponent.Controller
import model.Field
import model.Matrix
import model.Stone

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    var field = new Field(8, Stone.Empty)
    field = field.put(Stone.W, 4, 4)
    field = field.put(Stone.B, 4, 5)
    field = field.put(Stone.B, 5, 4)
    field = field.put(Stone.W, 5, 5)
    val controller = Controller(field)
    controller.movePossible.strat = 0
    val gui = GUI(controller)
    gui.run
    val tui = TUI(controller)
    tui.run
  }
}
