import controllerComponent.ControllerInterface
import fieldComponent.{Move, Stone}
import lib.{Event, Observer}
import play.api.libs.json.{JsValue, Json}

import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import scala.io.Source
import scala.language.postfixOps
import scala.swing.*
import scala.swing.event.*

class GUI(using controller: ControllerInterface) extends Frame, Observer {
  controller.add(this)

  def run(): Unit =
    update(Event.Move)
    gameloop

  def gameloop: Unit = None

  def analyseInput(input: String): Option[Move] = None

  private val lblFont = new Font("Arial", 0, 20)

  title = "Reversi"
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      })
      contents += new MenuItem(Action("Save") {
        controller.doAndPublish(controller.save)
      })
      contents += new MenuItem(Action("Load") {
        controller.doAndPublish(controller.load)
      })
      contents += new MenuItem(Action("Undo") {
        controller.doAndPublish(controller.undo)
      })
      contents += new MenuItem(Action("Redo") {
        controller.doAndPublish(controller.redo)
      })
    }
  }
  contents = new BorderPanel {
    val lbl: Label = new Label(getPlayerStateFromApi.toString + " ist an der Reihe")
    lbl.font = lblFont
    add(lbl, BorderPanel.Position.North)
    add(new CellPanel(getFieldSizeFromApi, getFieldSizeFromApi), BorderPanel.Position.Center)
  }
  pack()
  centerOnScreen()
  open()

  override def update(e: Event): Unit = e match {
    case Event.Quit => this.dispose
    case Event.Move => contents = new BorderPanel {
      val lbl: Label = new Label(getPlayerStateFromApi.toString + " ist an der Reihe")
      lbl.font = lblFont
      add(lbl, BorderPanel.Position.North)
      add(new CellPanel(getFieldSizeFromApi, getFieldSizeFromApi), BorderPanel.Position.Center)
    }
      controller.winner(controller.field)
      repaint
    case Event.End => contents = new BorderPanel {
      val lbl: Label = new Label(controller.winner(controller.field) + " hat gewonnen")
      lbl.font = lblFont
      add(lbl, BorderPanel.Position.North)
      add(new CellPanel(getFieldSizeFromApi, getFieldSizeFromApi), BorderPanel.Position.Center)
    }
      repaint
  }

  def getFieldSizeFromApi: Int = {
    val url = "http://localhost:8080/field/size" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val size: Int = (json \ "size").as[Int]
    size
  }

  def getStoneFromApi(row: Int, col: Int): Stone = {
    val url = s"http://localhost:8080/field/getStone?row=$row&col=$col" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val stoneValue: String = (json \ "stone").as[String]
    val stone: Stone = stoneValue match {
      case "□" => Stone.W
      case "■" => Stone.B
      case _ => Stone.Empty
    }
    stone
  }

  def getPlayerStateFromApi: Stone = {
    val url = "http://localhost:8080/field/playerState" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val playerStone: String = (json \ "playerStone").as[String]

    playerStone match {
      case "□" => Stone.W
      case "■" => Stone.B
      case _ => Stone.Empty
    }
  }

  private class CellPanel(r: Int, c: Int) extends GridPanel(r, c):
    private val list: List[CellButton] =
      for {
        i <- (1 to r).toList
        j <- 1 to c
        cb = CellButton(i, j, getStoneFromApi(i, j))
      } yield cb

    list.foreach(t => contents += t)

  private case class CellButton(r: Int, c: Int, var stone: Stone) extends Button():
    private val dim = new Dimension(90, 90)
    minimumSize = dim
    maximumSize = dim
    preferredSize = dim

    stone match {
      case Stone.W => icon = new ImageIcon(ImageIO.read(new File("gui/src/main/resources/White.png")))
      case Stone.B => icon = new ImageIcon(ImageIO.read(new File("gui/src/main/resources/Black.png")))
      case _ => icon = new ImageIcon(ImageIO.read(new File("gui/src/main/resources/Empty.png")))
    }

    font = new Font("Arial", 0, 30)
    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(src, pt, mod, clicks, props) =>
        val stone = getPlayerStateFromApi
        controller.doAndPublish(controller.put, Move(stone, r, c))
    }
}
