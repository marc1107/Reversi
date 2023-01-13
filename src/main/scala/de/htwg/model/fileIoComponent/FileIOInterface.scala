package de.htwg.model.fileIoComponent

import de.htwg.model.fieldComponent.FieldInterface

trait FileIOInterface {

  def load: FieldInterface
  def save(field: FieldInterface): Unit

}
