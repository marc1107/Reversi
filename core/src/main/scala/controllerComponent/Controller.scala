package controllerComponent

import fieldComponent.{FieldInterface, Move, Stone}
import fileIoComponent.FileIOInterface
import lib.Servers.{modelServer, persistenceServer}
import lib.{Event, MovePossible, Observable, PutCommand, UndoManager}
import play.api.libs.json.{JsValue, Json}

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source as SRC
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success}

class Controller(using var fieldC: FieldInterface, val fileIo: FileIOInterface) extends ControllerInterface() with Observable:
  private val undoManager = new UndoManager
  val movePossible: MovePossible = new MovePossible(this)

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val bootstrapServers = "localhost:9092"

  private val producerSettings =
    ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  def doAndPublish(doThis: Move => FieldInterface, move: Move): Unit =
    val t = movePossible.strategy(move) // returns a Try
    t match
      case Success(list) =>
        changePlayerStateWithApi
        fieldC = doThis(move)
        fieldC = putStoneAndGetFieldFromApi(fieldC, move.stone, move.r, move.c)
        list.foreach(el => fieldC = putStoneAndGetFieldFromApi(fieldC, el.stone, el.r, el.c))

        val records = new ListBuffer[ProducerRecord[String, String]]()

        records += new ProducerRecord("field-topic", "size", fieldC.size.toString)
        records += new ProducerRecord("field-topic", "playerState", getPlayerStateFromApi.toString)

        for (i <- 1 to fieldC.size)
          for (j <- 1 to fieldC.size)
            records += new ProducerRecord("field-topic", s"$i-$j", fieldC.get(i, j).toString)

        records += new ProducerRecord("field-topic", "end", "end")

        SRC(records.toList)
          .runWith(Producer.plainSink(producerSettings))
      case Failure(f) => println(f.getMessage)

    //notifyObservers(Event.Move)

  def doAndPublish(doThis: => FieldInterface): Unit =
    fieldC = doThis
    notifyObservers(Event.Move)

  def put(move: Move): FieldInterface =
    undoManager.doStep(fieldC, PutCommand(move, fieldC))

  def undo: FieldInterface =
    changePlayerStateWithApi
    val f = undoManager.undoStep(fieldC)
    putStoneAndGetFieldFromApi(f, f.get(1, 1), 1, 1)

  def redo: FieldInterface =
    changePlayerStateWithApi
    val f = undoManager.redoStep(fieldC)
    putStoneAndGetFieldFromApi(f, f.get(1, 1), 1, 1)

  def save: FieldInterface =
    saveapi(fieldC)
    fieldC

  def load: FieldInterface =
    val f: FieldInterface = loadFieldFromApi
    fieldC = putStoneAndGetFieldFromApi(f, f.get(1, 1), 1, 1)
    fieldC

  def field: FieldInterface = fieldC

  def winner(field: FieldInterface): String = Stone.B.toString

  override def toString: String = fieldC.toString

  private def loadFieldFromApi: FieldInterface =
    val url = s"http://$persistenceServer/fileio/load" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val jsonValue: JsValue = Json.parse(result)
    val fieldValue: JsValue = (jsonValue \ "field").as[JsValue]
    val playerr: Option[String] = (fieldValue \ "playerState").asOpt[String]

    val playerstone: Stone = playerr match {
      case Some("□") => Stone.W
      case Some("■") => Stone.B
      case _ => Stone.Empty
    }
    val field = fieldC.jsonToField(fieldValue.toString())
    if (getPlayerStateFromApi.toString != playerstone.toString) {
      changePlayerStateWithApi
    }
    field

  private def saveapi(field: FieldInterface): Unit = {
    val url = new URL(s"http://$persistenceServer/fileio") // replace with your API URL
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")

    val jsonInputString = field.toJsObjectPlayer.toString()
    val outputStreamWriter = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
    outputStreamWriter.write(jsonInputString)
    outputStreamWriter.close()
    
    connection.getResponseCode
  }

  def putStoneAndGetFieldFromApi(field: FieldInterface, stone: Stone, r: Int, c: Int): FieldInterface = {
    val url = new URL(s"http://$modelServer/field") // replace with your API URL
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")

    val jsonInputString = (field.toJsObject +
      ("stone" -> Json.toJson(stone.toString)) +
      ("row" -> Json.toJson(r)) +
      ("col" -> Json.toJson(c))
    ).toString()
  
    val outputStreamWriter = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
    outputStreamWriter.write(jsonInputString)
    outputStreamWriter.close()

    if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
      val streamReader = new InputStreamReader(connection.getInputStream)
      val reader = new BufferedReader(streamReader)
      val response = reader.readLine()
      reader.close()
      streamReader.close()

      val json: JsValue = Json.parse(response)
      val fieldValue: String = (json \ "field").as[JsValue].toString()
      field.jsonToField(fieldValue)
    } else {
      throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode)
    }
  }

  def getPlayerStateFromApi: Stone = {
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

  private def changePlayerStateWithApi: Int = {
    val url = s"http://$modelServer/field/changePlayerState" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val playerTurn: Int = (json \ "playersTurn").as[Int]

    playerTurn
  }
