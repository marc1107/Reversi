package de.htwg
package aview

import de.htwg.controller.controllerComponent.ControllerInterface
import de.htwg.model.Move
import de.htwg.util.Event

import scala.io.StdIn.readLine

class TUI(using controller: ControllerInterface) extends UI(controller):
  override def update(e: Event) = e match {
    case Event.Quit => sys.exit()
    case Event.Move =>
      println(controller.playerState.getStone.toString + " ist an der Reihe")
      println(controller.toString)
    case Event.End => println(controller.winner(controller.field) + " hat gewonnen")
  }

  override def gameloop: Unit =
    val input: String = readLine
    analyseInput(input) match
      case None =>
      case Some(move) => controller.doAndPublish(controller.put, move)
    gameloop

  /**
   * analyses the input from teh console and calls the controller
   *
   * @param input String
   * @return Option (Some(Move) or None)
   */
  override def analyseInput(input: String): Option[Move] =
    input match
      case "q" => sys.exit()
      case "u" => controller.doAndPublish(controller.undo); None
      case "r" => controller.doAndPublish(controller.redo); None
      case "s" => controller.doAndPublish(controller.save); None
      case "l" => controller.doAndPublish(controller.load); None
      case _ =>
        val chars = input.toCharArray
        val stone = controller.playerState.getStone
        val r = chars(0).toString.toInt
        val c = chars(1).toString.toInt
        Some(Move(stone, r, c))
