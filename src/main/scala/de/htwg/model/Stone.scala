package de.htwg.model

enum Stone(stringRepresentation: String):
  override def toString: String = stringRepresentation
  case B extends Stone("\u25A1")
  case W extends Stone("\u25A0")
  case Empty extends Stone(" ")
