package de.htwg.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class PlayerSpec extends AnyWordSpec {
  "A Player" should {
    val player = Player(name = "Lukas", stone = Stone.W)
    "have a name" in {
      player.name should be("Lukas")
    }
    "have initially zero points" in {
      player.points should be(0)
    }
    "have a stone" in {
      player.stone should be(Stone.W)
    }

  }

}
