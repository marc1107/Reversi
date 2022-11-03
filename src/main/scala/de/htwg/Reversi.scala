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

  /**
   * checks if the requested move is possible
   * @param arr game state array
   * @param x x coordinate
   * @param y y coordinate
   * @return true if the move is possible, false if not possible
   */
  def isMovePossible(arr: Array[Array[Int]], x: Int, y: Int, player: Int): Boolean = {
    // TODO: Abfrage so ändern dass genau der Spieler "player" einen Move machen darf (Steine drum herum prüfen)
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
    if (isMovePossible(arr, x, y, player)) {
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

  /**
   * returns and prints the game field at the game state
   * @param arr game state array
   * @param cellWidth width of cells
   * @return game field with game state
   */
  def printFilledField(arr: Array[Array[Int]], cellWidth: Int = 4): String = {
    var field: String = ""
    for (i <- arr.indices) {
      field = field + bar(cellNum = arr.length)
      for (j <- arr.indices) {
        if (arr(i)(j) == 1) {
          field = field + "|X" + " " * (cellWidth - 1)
        } else if (arr(i)(j) == 2) {
          field = field + "|O" + " " * (cellWidth - 1)
        } else {
          field = field + "|" + " " * cellWidth
        }
      }
      field = field + "|" + eol
    }
    field = field + bar(cellNum = arr.length)
    println(field)
    field
  }
}
