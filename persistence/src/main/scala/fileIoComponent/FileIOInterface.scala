package fileIoComponent

import fieldComponent.FieldInterface
import play.api.libs.json.JsValue
import playerStateComponent.PlayerState

trait FileIOInterface {

  def load: (FieldInterface, PlayerState)


  def save(field: FieldInterface): Unit
}
