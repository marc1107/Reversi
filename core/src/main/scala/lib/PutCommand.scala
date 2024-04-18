package lib

import fieldComponent.{FieldInterface, Move, Stone}
import play.api.libs.json.{JsValue, Json}

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}

class PutCommand(move: Move, var field: FieldInterface) extends Command:
  override def doStep(field: FieldInterface): FieldInterface = putStoneAndGetFieldFromApi(field, move.stone, move.r, move.c)

  override def undoStep(field: FieldInterface): FieldInterface =
    val fieldTemp: FieldInterface = this.field
    this.field = field
    fieldTemp

  override def redoStep(field: FieldInterface): FieldInterface =
    val fieldTemp: FieldInterface = this.field
    this.field = field
    fieldTemp

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
