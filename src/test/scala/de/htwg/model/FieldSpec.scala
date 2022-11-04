package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class FieldSpec extends AnyWordSpec {
  "A Reversi Field" when {
    "filled with B" should {
      val field1 = new Field(1, Stone.B)
      val field2 = new Field(2, Stone.B)
      val field3 = new Field(3, Stone.B)
      val eol = sys.props("line.separator")
      "have a bar as String of form '+---+---+---+'" in {
        field3.bar() should be("+---+---+---+" + eol)
      }
      "have a scalable bar" in {
        field1.bar(1, 1) should be("+-+" + eol)
        field2.bar(2, 1) should be("+--+" + eol)
        field2.bar(1, 2) should be("+-+-+" + eol)
      }
      "have cells as String of form '| B | B | B |'" in {
        field3.cells(0) should be("| B | B | B |" + eol)
      }
      "have scalable cells" in {
        field1.cells(0, 1) should be("|B|" + eol)
        field2.cells(0, 1) should be("|B|B|" + eol)
        field1.cells(0, 3) should be("| B |" + eol)
      }
      "have a mesh in the form " +
        "+-+" +
        "|B|" +
        "+-+" in {
        field1.mesh(1) should be("+-+" + eol + "|B|" + eol + "+-+" + eol)
        field2.mesh(1) should be("+-+-+" + eol + "|B|B|" + eol + "+-+-+" + eol + "|B|B|" + eol + "+-+-+" + eol)
      }
    }
    "filled with W" should {
      val field1 = new Field(1, Stone.W)
      val field2 = new Field(2, Stone.W)
      val field3 = new Field(3, Stone.W)
      val eol = sys.props("line.separator")
      "have a bar as String of form '+---+---+---+'" in {
        field3.bar() should be("+---+---+---+" + eol)
      }
      "have a scalable bar" in {
        field1.bar(1, 1) should be("+-+" + eol)
        field2.bar(2, 1) should be("+--+" + eol)
        field2.bar(1, 2) should be("+-+-+" + eol)
      }
      "have cells as String of form '| W | W | W |'" in {
        field3.cells(0) should be("| W | W | W |" + eol)
      }
      "have scalable cells" in {
        field1.cells(0, 1) should be("|W|" + eol)
        field2.cells(0, 1) should be("|W|W|" + eol)
        field1.cells(0, 3) should be("| W |" + eol)
      }
      "have a mesh in the form " +
        "+-+" +
        "|W|" +
        "+-+" in {
        field1.mesh(1) should be("+-+" + eol + "|W|" + eol + "+-+" + eol)
        field2.mesh(1) should be("+-+-+" + eol + "|W|W|" + eol + "+-+-+" + eol + "|W|W|" + eol + "+-+-+" + eol)
      }
    }
    "filled with Empty" should {
      val field = new Field(3, Stone.Empty)
      "be empty initially" in {
        field.toString should be( """#+---+---+---+
          #|   |   |   |
          #+---+---+---+
          #|   |   |   |
          #+---+---+---+
          #|   |   |   |
          #+---+---+---+
          #""" .stripMargin('#'))
      }
      "have a B and W after two moves" in {
        field.put(Stone.B, 0, 0).put(Stone.W, 1, 1).toString should be( """#+---+---+---+
          #| B |   |   |
          #+---+---+---+
          #|   | W |   |
          #+---+---+---+
          #|   |   |   |
          #+---+---+---+
          #""" .stripMargin('#'))
      }
    }
  }

}
