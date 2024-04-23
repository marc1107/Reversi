package fileIoComponent.fileIoXmlImpl

import fieldComponent.{Field, FieldInterface, Stone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import playerStateComponent.PlayerState

class FileIOSpec extends AnyFlatSpec with Matchers {
  "A FileIO" should "correctly save a game state" in {
    val fileIO = new FileIO
    val playerState = new PlayerState
    val field = new Field(8, Stone.Empty)

    // Save the game state
    fileIO.save(field)
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
    fileIO.saveString(field)
  }

  it should "correctly create an empty field" in {
    val fileIO = new FileIO

    // Create an empty field
    val field = fileIO.createEmptyField(8)

    // Assertions
    field.size should be (8)
  }

  it should "correctly convert a field to XML" in {
    val fileIO = new FileIO
    val playerState = new PlayerState
    val field = new Field(8, Stone.Empty)

    // Convert the field to XML
    val xml = fileIO.fieldToXml(field)

    // Assertions
    xml.label shouldBe "field"
  }
}