package fieldComponent

import com.google.inject.Inject
import matrixComponent.{Matrix, MatrixInterface}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import playerStateComponent.PlayerState

case class Field @Inject()(matrix: MatrixInterface[Stone]) extends FieldInterface:
  val playerState: PlayerState = PlayerState()
  
  def this(size: Int, filling: Stone) = this(new Matrix(size, filling))

  def size: Int = matrix.size

  val eol: String = sys.props("line.separator")

  def bar(cellWidth: Int = 3, cellNum: Int = 3): String = (("+" + "-" * cellWidth) * cellNum) + "+" + eol

  def cells(row: Int, cellWidth: Int = 3): String =
    matrix.row(row).map(_.toString).map(" " * ((cellWidth - 1) / 2) + _ + " " * ((cellWidth - 1) / 2)).mkString("|", "|", "|") + eol

  def mesh(cellWidth: Int = 3): String =
    (0 until size).map(cells(_, cellWidth)).mkString(bar(cellWidth, size), bar(cellWidth, size), bar(cellWidth, size))

  override def toString: String = mesh()

  def put(stone: Stone, r: Int, c: Int): Field = copy(matrix.replaceCell(r - 1, c - 1, stone))

  def get(r: Int, c: Int): Stone = matrix.cell(r - 1, c - 1)

  def toJsObject: JsObject = {
    Json.obj(
      "field" -> Json.obj(
        "size" -> JsNumber(this.size),
        "cells" -> Json.toJson(
          for {
            row <- 1 to this.size
            col <- 1 to this.size
          } yield {
            Json.obj(
              "row" -> row,
              "col" -> col,
              "cell" -> this.get(row, col).toString
            )
          }
        )
      )
    )
  }
  
  def jsonToField(jsonString: String): FieldInterface = {
    val json: JsValue = Json.parse(jsonString)
    val size: Int = (json \ "size").as[Int]
    val cells: Seq[JsValue] = (json \ "cells").as[Seq[JsValue]]
    var field: Field = new Field(size, Stone.Empty)
    cells.foreach(cell => {
      val row: Int = (cell \ "row").as[Int]
      val col: Int = (cell \ "col").as[Int]
      val cellValue: String = (cell \ "cell").as[String]
      val stone: Stone = cellValue match {
        case "□" => Stone.W
        case "■" => Stone.B
        case _ => Stone.Empty
      }

      field = field.put(stone, row, col)
    })
    field
  }

  override def getPlayerStone: Stone = playerState.getStone

  override def changePlayerState: Int = playerState.changeState
