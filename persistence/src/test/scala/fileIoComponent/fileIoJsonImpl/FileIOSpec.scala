package fileIoComponent.fileIoJsonImpl

import fileIoComponent.PlayerState
import fieldComponent.{Field, FieldInterface, Stone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsObject, Json}

class FileIOSpec extends AnyFlatSpec with Matchers {
  "A FileIO" should "correctly save a game state" in {
    val fileIO = new FileIO
    val playerState = new PlayerState
    val field = new Field(8, Stone.Empty)

    // Save the game state
    fileIO.save(field, playerState)
  }

  it should "correctly load a game state" in {
    val fileIO = new FileIO

    // Load the game state
    val (loadedField, loadedPlayerState) = fileIO.load

    // Assertions
    loadedField shouldBe a [FieldInterface]
    loadedPlayerState shouldBe a [PlayerState]
  }

  it should "correctly save a game state as a string" in {
    val fileIO = new FileIO
    val playerState = new PlayerState
    val field = new Field(8, Stone.Empty)

    // Save the game state as a string
    fileIO.saveString(field, playerState)
  }

  it should "correctly create an empty field" in {
    val fileIO = new FileIO

    // Create an empty field
    val field = fileIO.createEmptyField(8)

    // Assertions
    field.size should be (8)
  }

  it should "correctly convert a field to JSON" in {
    val fileIO = new FileIO
    val playerState = new PlayerState
    val field = new Field(8, Stone.Empty)

    // Convert the field to JSON
    val json = fileIO.fieldToJson(field, playerState)

    // Assertions
    (json \ "field").asOpt[JsObject] should not be empty
  }
}