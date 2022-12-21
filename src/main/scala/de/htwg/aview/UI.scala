package de.htwg
package aview

import controller.controllerComponent.ControllerInterface
import model.Move
import util.Observer
import util.Event

/**
 * template method design pattern
 * @param controller
 */
trait UI(using controller: ControllerInterface) extends Observer {
  controller.add(this)
  def run: Unit =
    update(Event.Move)
    gameloop
  def gameloop: Unit
  def analyseInput(input: String): Option[Move]
}
