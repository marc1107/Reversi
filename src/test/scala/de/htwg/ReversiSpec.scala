package de.htwg
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class ReversiSpec extends AnyWordSpec {
  "Reversi" should {
    "have a bar as String of form '+---+---+---+'" in {
      Reversi.bar() should be("+---+---+---+" + Reversi.eol)
    }
    "have cells as String of form '|   |   |   |'" in {
      Reversi.cells() should be("|   |   |   |" + Reversi.eol)
    }
  }
}
