import controllerComponent.ControllerInterface
import fieldComponent.Move
import lib.{Event, Observer}

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI(using controller: ControllerInterface) extends Observer:
  controller.add(this)

  def run(): Unit =
    update(Event.Move)
    gameloop
    
  override def update(e: Event): Unit = e match {
    case Event.Quit => sys.exit()
    case Event.Move =>
      println(controller.playerState.getStone.toString + " ist an der Reihe")
      println(controller.toString)
    case Event.End => println(controller.winner(controller.field) + " hat gewonnen")
  }

  def gameloop: Unit =
    val input: String = readLine
    analyseInput(input) match
      case None =>
      case Some(move) => controller.doAndPublish(controller.put, move)
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
          case "u" => controller.doAndPublish(controller.undo); None
          case "r" => controller.doAndPublish(controller.redo); None
          case "s" => controller.doAndPublish(controller.save); None
          case "l" => controller.doAndPublish(controller.load); None
          case _ =>
            val chars = value.toCharArray
            val stone = controller.playerState.getStone
            val r = chars(0).toString.toInt
            val c = chars(1).toString.toInt
            Some(Move(stone, r, c))
      case Failure(error) => println(error.getMessage); None

  private def inputSuccess(input: String): Try[String] =
    val pattern = "^(q|u|r|s|l|[1-8][1-8])$".r
    input match
      case pattern(_*) => Success(input)
      case _ => Failure(IllegalArgumentException("Invalid input"))
