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

  title = "TicTacToe"
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      })
    }
  }
  contents = new BorderPanel {
    add(new Label("Welcome to Reversi"), BorderPanel.Position.North)
    add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
  }
  pack()
  centerOnScreen()
  open()

  override def update =
    contents.foreach(f => if(f.isInstanceOf[BorderPanel]) {
      val bp: BorderPanel = f.asInstanceOf[BorderPanel]
      bp.contents.foreach(t => if(t.isInstanceOf[CellPanel]) {
        /*val cp: CellPanel = t.asInstanceOf[CellPanel]
        cp.contents.foreach(c => )*/
      })
    })
    repaint

  class CellPanel(r: Int, c: Int) extends GridPanel(r, c):
    var list: List[CellButton] = List()
    for (i <- 1 to r; j <- 1 to c) {
      list = list :+ CellButton(i, j, controller.field.get(i, j).toString)
    }
    list.foreach(t => contents += t)

    //def button(stone: String) = new Button(stone)

  class CellButton(r: Int, c: Int, stone: String) extends Button(stone):
    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(src, pt, mod, clicks, props) => {
        val stone = controller.playerState.getStone
        controller.doAndPublish(controller.put, Move(stone, r, c))
      }
    }
}
