package de.htwg
package aview

//import controller.modules.Default.given_ControllerInterface
import controller.controllerComponent.ControllerInterface
import model.Move

import scala.language.postfixOps
import scala.swing.*
import scala.swing.event.*
import util.Event

class GUI(controller: ControllerInterface) extends Frame with UI(controller){
  override def gameloop: Unit = None

  override def analyseInput(input: String): Option[Move] = None

  val lblFont = new Font("Arial", 0, 20)

  title = "Reversi"
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      })
    }
  }
  contents = new BorderPanel {
    val lbl: Label = new Label(controller.playerState.getStone.toString + " ist an der Reihe")
    lbl.font = lblFont
    add(lbl, BorderPanel.Position.North)
    add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
  }
  pack()
  centerOnScreen()
  open()

  override def update(e: Event) = e match {
    case Event.Quit => this.dispose
    case Event.Move => contents = new BorderPanel {
        val lbl: Label = new Label(controller.playerState.getStone.toString + " ist an der Reihe")
        lbl.font = lblFont
        add(lbl, BorderPanel.Position.North)
        add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
      }
      controller.winner(controller.field)
      repaint
    case Event.End => contents = new BorderPanel {
        val lbl: Label = new Label(controller.winner(controller.field) + " hat gewonnen")
        lbl.font = lblFont
        add(lbl, BorderPanel.Position.North)
        add(new CellPanel(controller.field.size, controller.field.size), BorderPanel.Position.Center)
      }
      repaint
  }

  class CellPanel(r: Int, c: Int) extends GridPanel(r, c):
    var list: List[CellButton] = List()
    for (i <- 1 to r; j <- 1 to c) {
      val cb : CellButton = CellButton(i, j, controller.field.get(i, j).toString)
      list = list :+ cb
    }
    list.foreach(t => contents += t)

  case class CellButton(r: Int, c: Int, var stone: String) extends Button(stone):
    val dim =  new Dimension(80, 80)
    minimumSize = dim
    maximumSize = dim
    preferredSize = dim
    font = new Font("Arial", 0, 30)
    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(src, pt, mod, clicks, props) => {
        val stone = controller.playerState.getStone
        controller.doAndPublish(controller.put, Move(stone, r, c))
      }
    }
}
