package de.htwg
package aview

import controller.Controller
import model.Stone
import scala.io.StdIn.readLine
import util.Observer

class TUI(controller: Controller) extends Observer {
  controller.add(this)
  def run =
    println(controller.field.toString)
    getInputAndPrintLoop()

  override def update = {}

  def getInputAndPrintLoop(): Unit =
    val input = readLine
    input match
      case "q" => None
      case _ =>
        val chars = input.toCharArray
        val stone = chars(0) match
          case 'B' => Stone.B
          case 'b' => Stone.B
          case 'W' => Stone.W
          case 'w' => Stone.W
          case _   => Stone.Empty
        val x = chars(1).toString.toInt
        val y = chars(2).toString.toInt
        controller.put(stone, x - 1, y - 1)
        println(controller.toString)
        getInputAndPrintLoop()
}
