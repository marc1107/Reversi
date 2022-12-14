package de.htwg.model.fieldComponent

import de.htwg.model.Stone

trait FieldInterface {
  def size: Int
  def bar(cellWidth: Int = 3, cellNum: Int = 3): String
  def cells(row: Int, cellWidth: Int = 3): String
  def mesh(cellWidth: Int = 3): String
  override def toString: String
  def put(stone: Stone, r: Int, c: Int): FieldInterface
  def get(r: Int, c: Int): Stone
}
