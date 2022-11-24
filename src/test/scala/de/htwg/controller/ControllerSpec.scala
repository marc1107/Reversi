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
    var field = new Field(3, Stone.Empty)
    field = field.put(Stone.B, 1, 1)
    field = field.put(Stone.W, 1, 2)
    val controller = Controller(field)
    "put a stone on the field when a move is made" in {
      val fieldWithMove = controller.put(Move(Stone.B, 1, 3))
      fieldWithMove.get(1, 3) should be(Stone.B)
      fieldWithMove.get(2, 1) should be(Stone.Empty)
    }
    "notify its observers on change" in {
      class TestObserver(controller: Controller) extends Observer:
        controller.add(this)
        var bing = false
        def update = bing = true
      val testObserver = TestObserver(controller)
      testObserver.bing should be(false)
      controller.doAndPublish(controller.put, Move(Stone.B, 1, 3))
      testObserver.bing should be(true)
    }
    "print a field" in {
      controller.toString should be( """#+---+---+---+
                                        #| □ | □ | □ |
                                        #+---+---+---+
                                        #|   |   |   |
                                        #+---+---+---+
                                        #|   |   |   |
                                        #+---+---+---+
                                        #""" .stripMargin('#'))
    }
  }
}