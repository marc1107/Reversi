package de.htwg

object Reversi {
  val eol: String = sys.props("line.separator")

  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    println(mesh(cellNum = 8))
  }

  def mesh(cellWidth: Int = 4, cellNum: Int = 2): String =
    (bar(cellWidth, cellNum) + cells(cellWidth, cellNum)) * cellNum + bar(cellWidth, cellNum)

  def bar(cellWidth: Int = 4, cellNum: Int = 4): String =
    ("+" + "-" * cellWidth) * cellNum + "+" + eol

  def cells(cellWidth: Int = 4, cellNum: Int = 4): String =
    ("|" + " " * cellWidth) * cellNum + "|" + eol
}
