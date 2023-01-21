package de.htwg.model.fileIoComponent

import de.htwg.controller.PlayerState
import de.htwg.model.fieldComponent.FieldInterface

trait FileIOInterface {

  def load: (FieldInterface, PlayerState)
  def save(field: FieldInterface, player: PlayerState): Unit

}
