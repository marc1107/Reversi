package de.htwg
package controller

import model.Field
import model.Move
import model.Stone
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import util.Observer
import util.Event

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
        def update(e: Event) = bing = true
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
    /*"use another strategy" in {
      controller.MovePossible.strat = 0
      val returnedList = controller.MovePossible.strategy(Move(Stone.B, 3, 3))
      returnedList.size should be (1)
      //returnedList(0) should be (Move(Stone.B, 3, 3))
    }*/
    "undo and redo a move" in {
      var field = controller.field
      field = controller.put(Move(Stone.Empty, 1, 1))
      field = controller.put(Move(Stone.Empty, 1, 2))
      field = controller.put(Move(Stone.Empty, 1, 3))
      field = controller.put(Move(Stone.B, 1, 2))
      field.get(1, 2) should be(Stone.B)
      field = controller.undo
      //field.get(1, 2) should be(Stone.Empty)
      field = controller.redo
      //field.get(1, 2) should be(Stone.B)
      controller.doAndPublish(controller.undo)
      controller.MovePossible.strategy1(Move(Stone.B, 1, 1))
    }
    "have failures" in {
      val stone_test_a = controller.playerState.getStone
      controller.doAndPublish(controller.put, Move(stone_test_a, 1, 1))
      controller.playerState.changeState
      val stone_test_b = controller.playerState.getStone
      controller.doAndPublish(controller.put, Move(stone_test_b, 3, 3))
    }
    "get a winner" in {
      val field = new Field(3, Stone.Empty)
      controller.winner(field.put(Stone.B, 1, 1)) should be {
        Stone.B.toString
      }
    }
  }
}