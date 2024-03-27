package de.htwg
package aview

import de.htwg.controller.controllerComponent.ControllerInterface
import de.htwg.model.Move
import de.htwg.util.{Event, Observer}

/**
 * template method design pattern
 *
 * @param controller
 */
trait UI(controller: ControllerInterface) extends Observer {
  controller.add(this)

  def run: Unit =
    update(Event.Move)
    gameloop

  def gameloop: Unit

  def analyseInput(input: String): Option[Move]
}
