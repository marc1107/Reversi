package de.htwg
package aview

import controller.Controller
import model.Move

import scala.language.postfixOps
import scala.swing.*
import scala.swing.event.*

class GUI(controller: Controller) extends Frame with UI(controller){
  override def gameloop: Unit = None

  override def analyseInput(input: String): Option[Move] = None

  title = "Reversi"
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      })
    }
  }
  contents = new BorderPanel {
    add(new Label(controller.playerState.getStone.toString + " ist an der Reihe"), BorderPanel.Position.North)
    add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
  }
  pack()
  centerOnScreen()
  open()

  override def update =
    contents = new BorderPanel {
      add(new Label(controller.playerState.getStone.toString + " ist an der Reihe"), BorderPanel.Position.North)
      add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
    }
    repaint

  class CellPanel(r: Int, c: Int) extends GridPanel(r, c):
    var list: List[CellButton] = List()
    for (i <- 1 to r; j <- 1 to c) {
      list = list :+ CellButton(i, j, controller.field.get(i, j).toString)
    }
    list.foreach(t => contents += t)

  case class CellButton(r: Int, c: Int, var stone: String) extends Button(stone):
    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(src, pt, mod, clicks, props) => {
        val stone = controller.playerState.getStone
        controller.doAndPublish(controller.put, Move(stone, r, c))
      }
    }
}
