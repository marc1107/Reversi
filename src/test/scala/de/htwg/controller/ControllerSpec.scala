package de.htwg
package controller

import model.Field
import model.Move
import model.Stone
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import util.Observer

class ControllerSpec extends AnyWordSpec {
  "The Controller" should {
    val controller = Controller(new Field(3, Stone.Empty))
    "put a stone on the field when a move is made" in {
      val fieldWithMove = controller.put(Move(Stone.B, 1, 2))
      fieldWithMove.get(1, 2) should be(Stone.B)
      fieldWithMove.get(1, 1) should be(Stone.Empty)
    }
    "notify its observers on change" in {
      class TestObserver(controller: Controller) extends Observer:
        controller.add(this)
        var bing = false
        def update = bing = true
      val testObserver = TestObserver(controller)
      testObserver.bing should be(false)
      controller.doAndPublish(controller.put, Move(Stone.B, 1, 2))
      testObserver.bing should be(true)
    }
    "print a field" in {
      val fieldWithMove = controller.put(Move(Stone.B, 1, 2))
      controller.toString should be( """#+---+---+---+
                                        #|   | □ |   |
                                        #+---+---+---+
                                        #|   |   |   |
                                        #+---+---+---+
                                        #|   |   |   |
                                        #+---+---+---+
                                        #""" .stripMargin('#'))
    }
  }
}