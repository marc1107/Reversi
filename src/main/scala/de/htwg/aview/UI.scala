package de.htwg.aview

import de.htwg.controller.Controller
import de.htwg.model.Move
import de.htwg.util.Observer

/**
 * template method design pattern
 * @param controller
 */
abstract class UI(controller: Controller) extends Observer {
  def run: Unit
  def getInputAndPrintLoop(): Unit
  def analyseInput(input: String): Option[Move]
}
