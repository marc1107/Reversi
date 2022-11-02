package de.htwg

import scala.io.StdIn._

object Reversi {
  val eol: String = sys.props("line.separator")

  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    requestPlayerName()
    println(mesh(cellNum = 8))
  }

  def mesh(cellWidth: Int = 4, cellNum: Int = 2): String =
    (bar(cellWidth, cellNum) + cells(cellWidth, cellNum)) * cellNum + bar(cellWidth, cellNum)

  def bar(cellWidth: Int = 4, cellNum: Int = 4): String =
    ("+" + "-" * cellWidth) * cellNum + "+" + eol

  def cells(cellWidth: Int = 4, cellNum: Int = 4): String =
    ("|" + " " * cellWidth) * cellNum + "|" + eol

  def gameRunning(arr: Array[Array[Int]]): Boolean =
    var running = false
    for(i <- arr.indices; j <- arr.indices) {
      if (arr(i)(j) == 0)
        running = true
    }
    running

  def requestPlayerName(): Array[String] =
    //println("Please type in Player Name 1")
    val playerOne = readLine("Please type in Player Name 1\n")
    //println("Please type in Player Name 2")
    val playerTwo = readLine("Please type in Player Name 2\n")

    val playerList = Array(playerOne, playerTwo)
    playerList
}
