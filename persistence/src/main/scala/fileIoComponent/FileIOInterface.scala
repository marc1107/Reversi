package fileIoComponent

import fieldComponent.FieldInterface
import playerStateComponent.PlayerState

trait FileIOInterface {

  def load: (FieldInterface, PlayerState)

  def save(field: FieldInterface): Unit

}
