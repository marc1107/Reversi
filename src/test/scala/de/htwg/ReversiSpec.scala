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
    "have a playerOne of 'Lukas\n' and a playerTwo 'Marc\n'" in {
      Reversi.requestPlayerName() should be(Array("Lukas", "Marc"))
    }
    "be runningGame of 'true'\n" in {
      Reversi.gameRunning(Array(Array(1,0), Array(1,1))) should be(true)
    }
  }
}
