package de.htwg

import de.htwg.model.Stone
import de.htwg.model.Matrix
import de.htwg.model.Field

import scala.io.StdIn.readLine

object Reversi {
  def main(args: Array[String]): Unit = {
    println("Welcome to Reversi")
    val field = new Field(4, Stone.Empty)
    println(field.toString)
    getInputAndPrintLoop(field)
  }

  def getInputAndPrintLoop(field: Field): Unit =
    val input = readLine
    parseInput(input) match
      case None => field
      case Some(newfield) =>
        println(newfield)
        getInputAndPrintLoop(newfield)

    def parseInput(input: String): Option[Field] =
      input match
        case "q" => None
        case _ =>
          val chars = input.toCharArray
          val stone = chars(0) match
            case 'B' => Stone.B
            case 'b' => Stone.B
            case 'W' => Stone.W
            case 'w' => Stone.W
            case _   => Stone.Empty
          val x = chars(1).toString.toInt
          val y = chars(2).toString.toInt
          Some(field.put(stone, x - 1, y - 1))
}
