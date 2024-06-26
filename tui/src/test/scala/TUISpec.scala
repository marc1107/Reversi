import fieldComponent.{Field, Move, Stone}
import fileIoComponent.fileIoXmlImpl.FileIO
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import tuiComponent.TUI

class TUISpec extends AnyWordSpec {

  "The tuiComponent.TUI" should {
    var field = new Field(3, Stone.Empty)
    field = field.put(Stone.B, 3, 1)
    field = field.put(Stone.W, 2, 1)
    val tui = TUI()
    "recognize the input 11 as move of stone B to field (1,1)" in {
      tui.analyseInput("11") should be(Some(Move(Stone.W, 1, 1)))
    }
  }
}