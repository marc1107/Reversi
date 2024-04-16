package fileIoComponent

import fieldComponent.FieldInterface

trait FileIOInterface {

  def load: (FieldInterface, PlayerState)

  def save(field: FieldInterface, player: PlayerState): Unit

}
