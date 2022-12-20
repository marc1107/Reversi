package de.htwg

import com.google.inject.Guice

import aview.TUI
import aview.GUI
import de.htwg.controller.controllerComponent.{Controller, ControllerInterface}
import de.htwg.model.fieldComponent.Field
import de.htwg.model.matrixComponent.Matrix
import model.Stone

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    /*var field = new Field(5, Stone.Empty)
    field = field.put(Stone.W, 4, 4)
    field = field.put(Stone.B, 4, 5)
    field = field.put(Stone.B, 5, 4)
    field = field.put(Stone.W, 5, 5)*/
    val injector = Guice.createInjector(new ReversiModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    /*val controller = new Controller(field)*/
    //controller.movePossible.strat = 0
    val gui = GUI(controller)
    gui.run
    val tui = TUI(controller)
    tui.run
  }
}
