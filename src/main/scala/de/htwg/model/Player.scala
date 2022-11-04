package de.htwg.model

case class Player(name: String, points: Int = 0, stone: Stone)

val player1 = Player(name = "Lukas", stone = Stone.W)
val player2 = Player(name = "Marc", stone = Stone.B)

object Player:
  val list = List(player1, player2)
  def next = list.iterator.next