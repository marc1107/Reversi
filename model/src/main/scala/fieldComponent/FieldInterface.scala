package fieldComponent

import play.api.libs.json.JsObject

trait FieldInterface {
  def size: Int

  def bar(cellWidth: Int, cellNum: Int): String

  def cells(row: Int, cellWidth: Int): String

  def mesh(cellWidth: Int): String

  override def toString: String

  def put(stone: Stone, r: Int, c: Int): FieldInterface

  def get(r: Int, c: Int): Stone
  
  def toJsObject: JsObject
  
  def jsonToField(json: String): FieldInterface
}
