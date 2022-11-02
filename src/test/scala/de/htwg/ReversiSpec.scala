package de.htwg

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class ReversiSpec extends AnyWordSpec {
  "Reversi" should {
    "have a bar as String of form '+----+----+----+----+'" in {
      Reversi.bar() should be("+----+----+----+----+" + Reversi.eol)
    }
    "have cells as String of form '|    |    |    |    |'" in {
      Reversi.cells() should be("|    |    |    |    |" + Reversi.eol)
    }
    "have mesh as String of form '+----+----+\n|    |    |\n+----+----+\n|    |    |\n+----+----+'" in {
      Reversi.mesh() should be("+----+----+" + Reversi.eol + "|    |    |" + Reversi.eol + "+----+----+" + Reversi.eol + "|    |    |" + Reversi.eol + "+----+----+" + Reversi.eol)
    }
    "have value as Boolean of form 'true'" in {
      Reversi.isMovePossible(Array(Array(0, 1), Array(2, 1)), 1, 1) should be(true)
    }
    "have value as Boolean of form 'false'" in {
      Reversi.isMovePossible(Array(Array(0, 1), Array(2, 1)), 2, 1) should be(false)
    }
    "have game state as Array of form 'Array((2,1),(2,1))'" in {
      Reversi.makeMove(Array(Array(0, 1), Array(2, 1)), 1, 1, 2) should be(Array(Array(2,1), Array(2,1)))
    }
    "have game state as Array of form 'Array((0,1),(2,1))'" in {
      Reversi.makeMove(Array(Array(0, 1), Array(2, 1)), 2, 1, 2) should be(Array(Array(0,1), Array(2,1)))
    }
    "have move as Array of form 'Array(0, 0)'" in {
      Reversi.askForNextMove(1, Array("Lukas", "Marc")) should be(Array(0, 0))
    }
  }
}
