package de.htwg
package aview

import controller.Controller
import model.Move
import model.Stone
import scala.io.StdIn.readLine
import util.Observer

class TUI(controller: Controller) extends UI(controller):
  controller.add(this)
  override def run =
    println(controller.field.toString)
    getInputAndPrintLoop()

  override def update = println(controller.field.toString)

  override def getInputAndPrintLoop(): Unit =
    val input: String = readLine
    analyseInput(input) match
      case None       =>
      case Some(move) => controller.doAndPublish(controller.put, move)
    getInputAndPrintLoop()

  override def analyseInput(input: String): Option[Move] =
    input match
      case "q" => sys.exit()
      case _ =>
        val chars = input.toCharArray
        /*val stone = chars(0) match
          case 'B' => Stone.B
          case 'b' => Stone.B
          case 'W' => Stone.W
          case 'w' => Stone.W
          case _   => Stone.Empty*/
        val stone = controller.playerState.getStone
        val x = chars(0).toString.toInt
        val y = chars(1).toString.toInt
        Some(Move(stone, x, y))
