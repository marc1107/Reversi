package GuiComponent

import fieldComponent.{Field, FieldInterface, Move, Stone}
import lib.Servers.{coreServer, modelServer}
import lib.{Event, Observer}
import play.api.libs.json.{JsValue, Json}
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import fieldComponent.Stone.Empty
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import playerStateComponent.PlayerState

import scala.concurrent.ExecutionContextExecutor
import java.io.{File, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import scala.io.Source
import scala.language.postfixOps
import scala.swing.*
import scala.swing.event.*

class GUI extends Frame {

  def run(): Unit =
    implicit val system: ActorSystem = ActorSystem("QuickStart")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val bootstrapServers = "localhost:9092"

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId("kafka-consumer-group-gui")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val topic = "field-topic"

    var field: FieldInterface = new Field(0, Stone.Empty)
    var playerState: Stone = Stone.Empty

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .map(record => (record.key, record.value))
      .to(Sink.foreach { case (key, value) =>
        key match {
          case "size" => field = new Field(value.toInt, Empty)
          case "playerState" => playerState = value match {
            case "□" => Stone.W
            case "■" => Stone.B
            case _ => Stone.Empty
          }
          case "end" => updateGui(field, playerState)
          case _ => // the key is in the format "i-j"
            val coordinates = key.split("-").map(_.toInt)
            val stone = value match {
              case "□" => Stone.W
              case "■" => Stone.B
              case _ => Stone.Empty
            }
            field = field.put(stone, coordinates(0), coordinates(1))
        }
      })
      .run()

    //update(Event.Move)
    gameloop

  def updateGui(field: FieldInterface, playerState: Stone): Unit = {
    contents = new BorderPanel {
      val lbl: Label = new Label(playerState.toString + " ist an der Reihe")
      lbl.font = lblFont
      add(lbl, BorderPanel.Position.North)
      add(new CellPanel2(field.size, field.size, field), BorderPanel.Position.Center)
    }
    repaint
  }

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
        saveWithApi()
        // controller.doAndPublish(controller.save)
      })
      contents += new MenuItem(Action("Load") {
        loadWithApi()
        // controller.doAndPublish(controller.load)
      })
      contents += new MenuItem(Action("Undo") {
        undoWithApi()
        // controller.doAndPublish(controller.undo)
      })
      contents += new MenuItem(Action("Redo") {
        redoWithApi()
        // controller.doAndPublish(controller.redo)
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

  def update(e: Event): Unit = e match {
    case Event.Quit => this.dispose
    case Event.Move => contents = new BorderPanel {
        val lbl: Label = new Label(getPlayerStateFromApi.toString + " ist an der Reihe")
        lbl.font = lblFont
        add(lbl, BorderPanel.Position.North)
        add(new CellPanel(getFieldSizeFromApi, getFieldSizeFromApi), BorderPanel.Position.Center)
      }
      repaint
    case Event.End => contents = new BorderPanel {
      val lbl: Label = new Label("Spieler X" + " hat gewonnen")
      lbl.font = lblFont
      add(lbl, BorderPanel.Position.North)
      add(new CellPanel(getFieldSizeFromApi, getFieldSizeFromApi), BorderPanel.Position.Center)
    }
      repaint
  }

  private def getFieldSizeFromApi: Int = {
    val url = s"http://$modelServer/field/size" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val size: Int = (json \ "size").as[Int]
    size
  }

  private def getStoneFromApi(row: Int, col: Int): Stone = {
    val url = s"http://$modelServer/field/getStone?row=$row&col=$col" // replace with your API URL
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

  private def getPlayerStateFromApi: Stone = {
    val url = s"http://$modelServer/field/playerState" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val playerStone: String = (json \ "playerStone").as[String]

    playerStone match {
      case "□" => Stone.W
      case "■" => Stone.B
      case _ => Stone.Empty
    }
  }
  
  def putMoveWithApi(move: Move): Unit = {
    val json = Json.obj(
      "method" -> "put",
      "stone" -> move.stone.toString,
      "row" -> move.r,
      "col" -> move.c
    )
    executePostToCore(json.toString())
  }
  
  def undoWithApi(): Unit = {
    val json = Json.obj(
      "method" -> "undo"
    )
    executePostToCore(json.toString())
  }
  
  def redoWithApi(): Unit = {
    val json = Json.obj(
      "method" -> "redo"
    )
    executePostToCore(json.toString())
  }
  
  def saveWithApi(): Unit = {
    val json = Json.obj(
      "method" -> "save"
    )
    executePostToCore(json.toString())
  }
  
  def loadWithApi(): Unit = {
    val json = Json.obj(
      "method" -> "load"
    )
    executePostToCore(json.toString())
  }
  
  private def executePostToCore(json: String): Boolean = {
    val url = new URL(s"http://$coreServer/core/doAndPublish") // replace with your API URL
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")

    val outputStreamWriter = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
    outputStreamWriter.write(json)
    outputStreamWriter.close()

    if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
      true
    } else {
      throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode)
    }
  }

  private class CellPanel(r: Int, c: Int) extends GridPanel(r, c){

    private val list: List[CellButton] =
    for {
      i <- (1 to r).toList
      j <- 1 to c
      cb = CellButton(i, j, getStoneFromApi(i, j))
    } yield cb

    list.foreach(t => contents += t)
  }

  private class CellPanel2(r: Int, c: Int, field: FieldInterface) extends GridPanel(r, c) {

    private val list: List[CellButton] =
      for {
        i <- (1 to r).toList
        j <- 1 to c
        cb = CellButton(i, j, field.get(i, j))
      } yield cb

    list.foreach(t => contents += t)
  }

  private case class CellButton(r: Int, c: Int, var stone: Stone) extends Button(){
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
        putMoveWithApi(Move(stone, r, c))
        //controller.doAndPublish(controller.put, Move(stone, r, c))
    }
  }

}
