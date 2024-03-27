package de.htwg
package controller

import de.htwg.controller.controllerComponent.ControllerInterface
import de.htwg.model.{Move, Stone}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class MovePossible(controller: ControllerInterface) {
  var strat = 1
  val strategy: Move => Try[ListBuffer[Move]] =
    if (strat == 0) strategy1 else strategy2

  def strategy1(move: Move): Try[ListBuffer[Move]] =
    controller.field.get(move.r, move.c) == Stone.Empty
    val lb = new ListBuffer[Move]
    lb.append(move)
    Success(lb)


  def strategy2(move: Move): Try[ListBuffer[Move]] =
    def isInsideField(r: Int, c: Int): Boolean = {
      r >= 1 && r <= controller.field.size && c >= 1 && c <= controller.field.size
    }

    def outflankedInDir(row: Int, col: Int, cDelta: Int, rDelta: Int): ListBuffer[Move] = {
      val outflanked = ListBuffer[Move]()
      var r: Int = row + rDelta
      var c: Int = col + cDelta

      while (isInsideField(r, c) && controller.field.get(r, c) != Stone.Empty) {
        // wenn gegnerischer Stein
        if (controller.field.get(r, c) != controller.playerState.getStone)
          outflanked += Move(controller.playerState.getStone, r, c)
          r += rDelta
          c += cDelta
        else
          return outflanked
      }
      new ListBuffer[Move]
    }

    def outFlanked(r: Int, c: Int): ListBuffer[Move] = {
      val outflanked = ListBuffer[Move]()

      for (rDelta <- -1 to 1; cDelta <- -1 to 1) {
        if (rDelta != 0 || cDelta != 0)
          outflanked.appendAll(outflankedInDir(r, c, cDelta, rDelta))
      }

      outflanked
    }

    controller.field.get(move.r, move.c) match {
      case Stone.Empty =>
        val outflanked = outFlanked(move.r, move.c)
        outflanked.isEmpty match
          case false => Success(outflanked)
          case _ => Failure(new Exception("Nothing to turn"))
      case _ => Failure(new Exception("Cell not empty"))
    }
}
