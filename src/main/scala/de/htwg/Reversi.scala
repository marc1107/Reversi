package de.htwg

import de.htwg.Default.given
import de.htwg.aview.{GUI, TUI}

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    new GUI().run
    new TUI().run
  }
}
