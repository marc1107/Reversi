package de.htwg.aview

import de.htwg.controller.Controller
import de.htwg.model.Move
import de.htwg.util.Observer
import de.htwg.util.Event

/**
 * template method design pattern
 * @param controller
 */
trait UI(controller: Controller) extends Observer {
  controller.add(this)
  def run: Unit =
    update(Event.Move)
    gameloop
  def gameloop: Unit
  def analyseInput(input: String): Option[Move]
}
