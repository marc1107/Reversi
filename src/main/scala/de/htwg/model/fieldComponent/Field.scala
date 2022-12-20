package de.htwg.model.fieldComponent

import de.htwg.model.Stone
import de.htwg.model.matrixComponent.{Matrix, MatrixInterface}

import com.google.inject.Inject

case class Field @Inject() (matrix: MatrixInterface[Stone]) extends FieldInterface :
  def this(size: Int, filling: Stone) = this(new Matrix(size, filling))

  def size = matrix.size

  val eol = sys.props("line.separator")

  def bar(cellWidth: Int = 3, cellNum: Int = 3): String = (("+" + "-" * cellWidth) * cellNum) + "+" + eol

  def cells(row: Int, cellWidth: Int = 3): String =
    matrix.row(row).map(_.toString).map(" " * ((cellWidth - 1) / 2) + _ + " " * ((cellWidth - 1) / 2)).mkString("|", "|", "|") + eol

  def mesh(cellWidth: Int = 3): String =
    (0 until size).map(cells(_, cellWidth)).mkString(bar(cellWidth, size), bar(cellWidth, size), bar(cellWidth, size))

  override def toString: String = mesh()

  def put(stone: Stone, r: Int, c: Int): Field = copy(matrix.replaceCell(r - 1, c - 1, stone))

  def get(r: Int, c: Int): Stone = matrix.cell(r - 1, c - 1)
