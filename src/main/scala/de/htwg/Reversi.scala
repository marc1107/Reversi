package de.htwg

import aview.TUI
import controller.Controller
import model.Field
import model.Matrix
import model.Stone

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    val field = new Field(4, Stone.Empty)
    val controller = Controller(field)
    val tui = TUI(controller)
    tui.run
  }
}
