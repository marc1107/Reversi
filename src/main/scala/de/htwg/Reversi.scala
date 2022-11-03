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

  /**
   * checks if the game is running
   * @param arr game state array
   * @return true if the game is running, false if the game is at end state
   */
  def gameRunning(arr: Array[Array[Int]]): Boolean =
    var running = false
    for(i <- arr.indices; j <- arr.indices) {
      if (arr(i)(j) == 0)
        running = true
    }
    running

  /**
   * request player names for player one and player two
   * @return array of type String with the two player names
   */
  def requestPlayerName(): Array[String] =
    val playerOne = readLine("Please type in Player Name 1\n")
    val playerTwo = readLine("Please type in Player Name 2\n")

    val playerList = Array(playerOne, playerTwo)
    playerList
}
