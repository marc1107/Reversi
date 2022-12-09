package de.htwg
package util

trait Observer {
  def update(e: Event): Unit
}

class Observable {
  var subscribers: Vector[Observer] = Vector()

  def add(s: Observer) = subscribers = subscribers :+ s

  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)

  def notifyObservers(e: Event) = subscribers.foreach(o => o.update(e))
}

enum Event:
  case Quit
  case Move
  case End