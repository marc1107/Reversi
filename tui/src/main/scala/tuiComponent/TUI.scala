package tuiComponent

import fieldComponent.Stone.Empty
import fieldComponent.{Field, FieldInterface, Move, Stone}
import lib.Event
import lib.Servers.{coreServer, modelServer}
import play.api.libs.json.{JsValue, Json}
import scala.jdk.CollectionConverters.*

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import scala.concurrent.ExecutionContextExecutor

import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}
import scala.io.Source
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI:

  def run(): Unit =
    implicit val system: ActorSystem = ActorSystem("QuickStart")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val bootstrapServers = "localhost:9092"

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId("kafka-consumer-group-tui")
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
          case "end" => printCurrentState(field, playerState)
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
    
  def update(e: Event): Unit = e match {
    case Event.Quit => sys.exit()
    case Event.Move =>
      println(getPlayerStateFromApi.toString + " ist an der Reihe")
      println(getFieldFromApi.toString)
    case Event.End => println("Spieler X" + " hat gewonnen")
  }

  def printCurrentState(field: FieldInterface, playerState: Stone) =
    println(playerState.toString + " ist an der Reihe")
    println(field.toString)

  def gameloop: Unit =
    val input: String = readLine
    analyseInput(input) match
      case None =>
      case Some(move) => putMoveWithApi(move)
    gameloop

  /**
   * analyses the input from teh console and calls the controller
   *
   * @param consoleIn console input
   * @return Option (Some(fieldComponent.Move) or None)
   */
  def analyseInput(consoleIn: String): Option[Move] =
    val input = inputSuccess(consoleIn)
    input match
      case Success(value) =>
        value match
          case "q" => sys.exit()
          case "u" => undoWithApi(); None
          case "r" => redoWithApi(); None
          case "s" => saveWithApi(); None
          case "l" => loadWithApi(); None
          case _ =>
            val chars = value.toCharArray
            val stone = getPlayerStateFromApi
            val r = chars(0).toString.toInt
            val c = chars(1).toString.toInt
            Some(Move(stone, r, c))
      case Failure(error) => println(error.getMessage); None

  private def inputSuccess(input: String): Try[String] =
    val pattern = "^(q|u|r|s|l|[1-8][1-8])$".r
    input match
      case pattern(_*) => Success(input)
      case _ => Failure(IllegalArgumentException("Invalid input"))

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
  
  private def getFieldFromApi: FieldInterface = {
    val url = s"http://$modelServer/field" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val fieldJson: JsValue = (json \ "field").get
    val size: Int = (fieldJson \ "size").as[Int]
    val cellsJson: Seq[JsValue] = (fieldJson \ "cells").as[Seq[JsValue]]
  
    var field: Field = new Field(size, Stone.Empty)
  
    for (cellJson <- cellsJson) {
      val row: Int = (cellJson \ "row").as[Int]
      val col: Int = (cellJson \ "col").as[Int]
      val cellValue: String = (cellJson \ "cell").as[String]
      val stone: Stone = cellValue match {
        case "□" => Stone.W
        case "■" => Stone.B
        case _ => Stone.Empty
      }
      field = field.put(stone, row, col)
    }
  
    field
  }
