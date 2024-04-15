package matrixComponent

import com.google.inject.Inject

case class Matrix[T] @Inject()(rows: Vector[Vector[T]]) extends MatrixInterface[T]:
  def this(size: Int, filling: T) = this(Vector.tabulate(size, size) { (row, col) => filling })

  def size: Int = rows.size

  def cell(row: Int, col: Int): T = rows(row)(col)

  def row(row: Int): Vector[T] = rows(row)

  def fill(filling: T): Matrix[T] = copy(Vector.tabulate(size, size) { (row, col) => filling })

  def replaceCell(row: Int, col: Int, cell: T): Matrix[T] = copy(rows.updated(row, rows(row).updated(col, cell)))
