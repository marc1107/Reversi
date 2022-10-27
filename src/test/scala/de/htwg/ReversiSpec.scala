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
      Reversi.mesh() should be("+----+----+\n|    |    |\n+----+----+\n|    |    |\n+----+----+" + Reversi.eol)
    }
  }
}
