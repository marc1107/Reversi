package de.htwg.model.matrixComponent


trait MatrixInterface[T] {
  def size: Int
  def cell(row: Int, col: Int): T
  def row(row: Int): Vector[T]
  def fill(filling: T): MatrixInterface[T]
  def replaceCell(row: Int, col: Int, cell: T): MatrixInterface[T]
}
