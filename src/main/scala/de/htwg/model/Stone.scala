package de.htwg.model

enum Stone(stringRepresentation: String):
  override def toString: String = stringRepresentation
  case B extends Stone("B")
  case W extends Stone("W")
  case Empty extends Stone(" ")
