package lib

import controllerComponent.ControllerInterface
import fieldComponent.{Move, Stone}
import play.api.libs.json.{JsValue, Json}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success, Try}

class MovePossible(controller: ControllerInterface) {
  private val strat = 1
  val strategy: Move => Try[ListBuffer[Move]] =
    strategy2

  private def strategy2(move: Move): Try[ListBuffer[Move]] =
    def isInsideField(r: Int, c: Int): Boolean = {
      val fieldSize = getFieldSizeFromApi
      r >= 1 && r <= fieldSize && c >= 1 && c <= fieldSize
    }

    def outflankedInDir(row: Int, col: Int, cDelta: Int, rDelta: Int): ListBuffer[Move] = {
      @tailrec
      def outflankedInDirRec(r: Int, c: Int, outflanked: ListBuffer[Move]): ListBuffer[Move] = {
        if (!isInsideField(r, c) || getStoneFromApi(r, c) == Stone.Empty) {
          ListBuffer.empty[Move]
        } else if (getStoneFromApi(r, c) == getPlayerStateFromApi) {
          outflanked
        } else {
          outflankedInDirRec(r + rDelta, c + cDelta, outflanked :+ Move(getPlayerStateFromApi, r, c))
        }
      }
    
      outflankedInDirRec(row + rDelta, col + cDelta, ListBuffer.empty[Move])
    }

    def outFlanked(r: Int, c: Int): ListBuffer[Move] = {
      val outflanked = ListBuffer[Move]()

      for (rDelta <- -1 to 1; cDelta <- -1 to 1) {
        if (rDelta != 0 || cDelta != 0)
          outflanked.appendAll(outflankedInDir(r, c, cDelta, rDelta))
      }

      outflanked
    }

    def getFieldSizeFromApi: Int = {
      val url = "http://0.0.0.0:8080/field/size" // replace with your API URL
      val result = Source.fromURL(url).mkString
      val json: JsValue = Json.parse(result)
      val size: Int = (json \ "size").as[Int]
      size
    }

    def getStoneFromApi(row: Int, col: Int): Stone = {
      val url = s"http://0.0.0.0:8080/field/getStone?row=$row&col=$col" // replace with your API URL
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
      val url = "http://0.0.0.0:8080/field/playerState" // replace with your API URL
      val result = Source.fromURL(url).mkString
      val json: JsValue = Json.parse(result)
      val playerStone: String = (json \ "playerStone").as[String]

      playerStone match {
        case "□" => Stone.W
        case "■" => Stone.B
        case _ => Stone.Empty
      }
    }

    getStoneFromApi(move.r, move.c) match {
      case Stone.Empty =>
        val outflanked = outFlanked(move.r, move.c)
        outflanked.isEmpty match
          case false => Success(outflanked)
          case _ => Failure(new Exception("Nothing to turn"))
      case _ => Failure(new Exception("Cell not empty"))
    }
}
