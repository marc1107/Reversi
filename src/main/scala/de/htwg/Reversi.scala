package de.htwg

import scala.io.StdIn._

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

  /**
   * checks if the requested move is possible
   * @param arr game state array
   * @param x x coordinate
   * @param y y coordinate
   * @return true if the move is possible, false if not possible
   */
  def isMovePossible(arr: Array[Array[Int]], x: Int, y: Int): Boolean = {
    if (x > arr.length || y > arr(0).length) {
      false
    } else {
      arr(x-1)(y-1) == 0
    }
  }

  /**
   * Makes a move for the player in the game state array
   * @param arr game state array
   * @param x x coordinate
   * @param y y coordinate
   * @param player the player whose turn it is
   * @return game state array including the players move
   */
  def makeMove(arr: Array[Array[Int]], x: Int, y: Int, player: Int): Array[Array[Int]] = {
    if (isMovePossible(arr, x, y)) {
      arr(x-1)(y-1) = player
    }
    arr
  }

  /**
   * Asks the player whose turn it is to input his move
   * @param player the player whose turn it is
   * @param names an Array with the size 2 which contains the Names of the 2 players
   * @return Array with the size 2 which contains the x and y coordinate the player choose
   */
  def askForNextMove(player: Int, names: Array[String]): Array[Int] = {
    if (names.length < 2)
      throw new Exception("2 Players required")

    println(names(player-1) + " ist an der Reihe")
    println("x eingeben:")
    val x = readInt()
    println("y eingeben:")
    val y = readInt()
    val move = Array(x, y)
    move
  }
}
