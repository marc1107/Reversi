package de.htwg
package aview

import de.htwg.controller.controllerComponent
import de.htwg.controller.controllerComponent.Controller
import de.htwg.model.fieldComponent.Field
import model.Move
import model.Stone
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TUISpec extends AnyWordSpec {

  "The TUI" should {
    var field = new Field(3, Stone.Empty)
    field = field.put(Stone.B, 3, 1)
    field = field.put(Stone.W, 2, 1)
    val tui = TUI(controllerComponent.Controller(field))
    "recognize the input 11 as move of stone B to field (1,1)" in {
      tui.analyseInput("11") should be(Some(Move(Stone.B, 1, 1)))
    }
  }
}