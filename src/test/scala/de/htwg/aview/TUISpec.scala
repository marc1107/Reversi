package de.htwg
package aview

import controller.Controller
import model.Field
import model.Move
import model.Stone
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class TUISpec extends AnyWordSpec {

  "The TUI" should {
    val tui = TUI(Controller(new Field(3, Stone.Empty)))
    "recognize the input b12 as move of stone B to field (1,2)" in {
      tui.analyseInput("b12") should be(Some(Move(Stone.B, 1, 2)))
    }
  }
}