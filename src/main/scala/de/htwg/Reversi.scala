package de.htwg

import com.google.inject.Guice

import aview.TUI
import aview.GUI
import controller.controllerComponent.{Controller, ControllerInterface}
import model.fieldComponent.Field
import model.matrixComponent.Matrix
import model.Stone
import controller.modules.Default.{given}

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    new GUI().run
    new TUI().run
  }
}
