package controllerComponent

import fieldComponent.{FieldInterface, Move, Stone}
import fileIoComponent.FileIOInterface
import lib.{Event, MovePossible, Observable, PutCommand, UndoManager}
import play.api.libs.json.{JsObject, JsValue, Json}
import playerStateComponent.PlayerState

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success}

class Controller(using var fieldC: FieldInterface, val fileIo: FileIOInterface) extends ControllerInterface() with Observable:
  private val undoManager = new UndoManager
  val movePossible: MovePossible = new MovePossible(this)

  def doAndPublish(doThis: Move => FieldInterface, move: Move): Unit =
    val t = movePossible.strategy(move) // returns a Try
    t match
      case Success(list) =>
        changePlayerStateWithApi
        fieldC = doThis(move)
        fieldC = putStoneAndGetFieldFromApi(fieldC, move.stone, move.r, move.c)
        //fieldC = fieldC.put(move.stone, move.r, move.c)
        list.foreach(el => fieldC = putStoneAndGetFieldFromApi(fieldC, el.stone, el.r, el.c))
      case Failure(f) => println(f.getMessage)

    notifyObservers(Event.Move)

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
    val playerState = PlayerState()
    if (playerState.getStone.toString != getPlayerStateFromApi.toString) {
      playerState.changeState
    }
    saveapi(fieldC,playerState)
    fieldC

  def load: FieldInterface =
    val f: FieldInterface = loadFieldFromApi
    fieldC = putStoneAndGetFieldFromApi(f, f.get(1, 1), 1, 1)
    fieldC

  def field: FieldInterface = fieldC

  def winner(field: FieldInterface): String = Stone.B.toString

  override def toString: String = fieldC.toString

  private def loadFieldFromApi: FieldInterface =
    val url = "http://localhost:8081/fileio/load" // replace with your API URL
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

  private def saveapi(field: FieldInterface, playerState: PlayerState): Unit = {
    val url = new URL("http://localhost:8081/fileio") // replace with your API URL
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.setRequestProperty("Content-Type", "application/json")

    val jsonInputString = field.toJsObjectPlayer(playerState).toString()
    val outputStreamWriter = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
    outputStreamWriter.write(jsonInputString)
    outputStreamWriter.close()
    
    connection.getResponseCode
  }

  def putStoneAndGetFieldFromApi(field: FieldInterface, stone: Stone, r: Int, c: Int): FieldInterface = {
    val url = new URL("http://localhost:8080/field") // replace with your API URL
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

  def changePlayerStateWithApi: Int = {
    val url = "http://localhost:8080/field/changePlayerState" // replace with your API URL
    val result = Source.fromURL(url).mkString
    val json: JsValue = Json.parse(result)
    val playerTurn: Int = (json \ "playersTurn").as[Int]

    playerTurn
  }
