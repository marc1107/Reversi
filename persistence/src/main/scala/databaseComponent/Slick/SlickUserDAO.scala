package databaseComponent.Slick

import databaseComponent.Slick.Tables.{BoardTable, FieldTable, PlayerStateTable}
import databaseComponent.UserDAO
import play.api.libs.json.{JsValue, Json}
import playerStateComponent.PlayerState
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.*
import slick.jdbc.JdbcBackend.Database
import scala.io.StdIn

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class SlickUserDAO extends UserDAO {

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "tbl")
  private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
  private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "localhost")
  private val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val playerState = TableQuery[PlayerStateTable]
  val field = TableQuery[FieldTable]
  val board = TableQuery[BoardTable]

  override def createTables(): Future[Unit] = {
    val createPlayerStateTabelAction = playerState.schema.createIfNotExists
    val createBoardTableAction = board.schema.createIfNotExists
    val createFieldTableAction = field.schema.createIfNotExists

    val combinedAction = for {
      _ <- createPlayerStateTabelAction
      _ <- createBoardTableAction
      _ <- createFieldTableAction
    } yield ()

    database.run(combinedAction)

  }

  override def dropTables(): Future[Unit] = {
    val dropPlayerStateTabelAction = playerState.schema.dropIfExists
    val dropBoardTableAction = board.schema.dropIfExists
    val dropFieldTableAction = field.schema.dropIfExists

    val combinedAction = for {
      _ <- dropPlayerStateTabelAction
      _ <- dropBoardTableAction
      _ <- dropFieldTableAction
    } yield ()

    database.run(combinedAction)
  }

  // for size
  private def insertBoard(size: Int): Future[Int] = {
    database.run((board returning board.map(_.boardId)) += (0, size))
  }

  private def insertPlayerState(playerStateValue: String): Future[Int] = {
    database.run((playerState returning playerState.map(_.playerStateId)) += (0, playerStateValue))
  }

  def insertField(boardId: Int, playerStateId: Int, fieldCells: Seq[JsValue]): Future[Int] = {
    val fieldInsertions = fieldCells.map { cell =>
      val r = (cell \ "row").as[Int]
      val c = (cell \ "col").as[Int]
      val stone = (cell \ "cell").as[String]
      (0, boardId, playerStateId, r, c, stone)
    }
    database.run((field returning field.map(_.fieldId)) ++= fieldInsertions)
  }

  override def save(game: String): Future[Int] = {
    Try(Json.parse(game)) match {
      case Failure(exception) =>
        Future.failed(new IllegalArgumentException("Invalid JSON!"))
      case Success(json) =>
        val size: Int = (json \ "field" \ "size").as[Int]
        val playerState: String = (json \ "field" \ "playerState").as[String]
        val fieldCells: Seq[JsValue] = (json \ "field" \ "cells").asOpt[Seq[JsValue]].getOrElse(Seq.empty)

        /*val gameStateType = (data \ "gameState" \ "type").asOpt[String].getOrElse("")
        val boardFields = (data \ "gameState" \ "game" \ "board" \ "fields").asOpt[Seq[JsValue]].getOrElse(Seq.empty)
        val boardSize = (data \ "gameState" \ "game" \ "board" \ "size").asOpt[Int].getOrElse(0)
        val players = (data \ "gameState" \ "game" \ "players").asOpt[Seq[JsValue]].getOrElse(Seq.empty)
        val currentPlayerName = (data \ "gameState" \ "game" \ "currentPlayer" \ "name").asOpt[String].getOrElse("")
        val currentPlayerColor = (data \ "gameState" \ "game" \ "currentPlayer" \ "color").asOpt[String].getOrElse("")
        val setStones = (data \ "gameState" \ "game" \ "setStones").asOpt[Int].getOrElse(0)
*/
        for {
          boardId <- insertBoard(size)
          playerStateId <- insertPlayerState(playerState)
          fieldId <- insertField(boardId, playerStateId, fieldCells)
        } yield fieldId
    }
  }

  override def load(): Future[Option[String]] = {
    ???
  }

  override def closeDatabase(): Unit = {
    database.close()
  }
}
