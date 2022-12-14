package de.htwg.model

import de.htwg.model.fieldComponent.FieldInterface
import matrixComponent.{Matrix, MatrixInterface}

case class Field(matrix: MatrixInterface[Stone]) extends FieldInterface:
  def this(size: Int, filling: Stone) = this(new Matrix(size, filling))
  def size = matrix.size
  val eol = sys.props("line.separator")
  def bar(cellWidth: Int = 3, cellNum: Int = 3): String = (("+" + "-" * cellWidth) * cellNum) + "+" + eol
  def cells(row: Int, cellWidth: Int = 3): String =
    matrix.row(row).map(_.toString).map(" " * ((cellWidth - 1) / 2) + _ + " " * ((cellWidth - 1) / 2)).mkString("|", "|", "|") + eol
  def mesh(cellWidth: Int = 3): String =
    (0 until size).map(cells(_, cellWidth)).mkString(bar(cellWidth, size), bar(cellWidth, size), bar(cellWidth, size))
  override def toString = mesh()
  def put(stone: Stone, r: Int, c: Int): Field = copy(matrix.replaceCell(r-1, c-1, stone))
  def get(r: Int, c: Int): Stone = matrix.cell(r-1, c-1)