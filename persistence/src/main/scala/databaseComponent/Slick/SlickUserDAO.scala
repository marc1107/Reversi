package databaseComponent.Slick

import databaseComponent.Slick.Tables.{BoardTable, FieldTable, PlayerStateTable}
import databaseComponent.UserDAO
import lib.Servers.postgresServer
import play.api.libs.json.Json
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class SlickUserDAO extends UserDAO {
  private val postgresServerParts = postgresServer.split(":")
  private val host = postgresServerParts(0)
  private val port = postgresServerParts(1)

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "postgres")
  private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", port)
  private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", host)
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

  override def create(): Future[Unit] = {
    val createPlayerStateTableAction = playerState.schema.createIfNotExists
    val createBoardTableAction = board.schema.createIfNotExists
    val createFieldTableAction = field.schema.createIfNotExists

    val combinedAction = for {
      _ <- createBoardTableAction
      _ <- createPlayerStateTableAction
      _ <- createFieldTableAction
    } yield ()

    database.run(combinedAction)

  }

  override def delete(): Future[Unit] = {
    val dropPlayerStateTableAction = playerState.schema.dropIfExists
    val dropBoardTableAction = board.schema.dropIfExists
    val dropFieldTableAction = field.schema.dropIfExists

    val combinedAction = for {
      _ <- dropFieldTableAction
      _ <- dropPlayerStateTableAction
      _ <- dropBoardTableAction
    } yield ()

    database.run(combinedAction)
  }

  private def insertBoard(size: Int): Future[Int] = {
    database.run((board returning board.map(_.boardId)) += (0, size))
  }

  private def insertPlayerState(playerStateValue: String): Future[Int] = {
    database.run((playerState returning playerState.map(_.playerStateId)) += (0, playerStateValue))
  }

  private def insertCells(boardId: Int, playerStateId: Int, fieldCells: Seq[JsValue]): Future[Option[Int]] = {
    val fieldInsertions = fieldCells.map { cell =>
      val r = (cell \ "row").as[Int]
      val c = (cell \ "col").as[Int]
      val stone = (cell \ "cell").as[String]
      (0, boardId, playerStateId, r, c, stone)
    }
    database.run(field ++= fieldInsertions)
  }

  override def save(game: String): Future[Int] = {
    Try(Json.parse(game)) match {
      case Failure(exception) =>
        Future.failed(new IllegalArgumentException("Invalid JSON!"))
      case Success(json) =>
        val size: Int = (json \ "field" \ "size").as[Int]
        val playerState: String = (json \ "field" \ "playerState").as[String]
        val fieldCells: Seq[JsValue] = (json \ "field" \ "cells").asOpt[Seq[JsValue]].getOrElse(Seq.empty)

        for {
          boardId <- insertBoard(size)
          playerStateId <- insertPlayerState(playerState)
          fieldId <- insertCells(boardId, playerStateId, fieldCells)
        } yield fieldId.get
    }
  }

  override def load(): Future[Option[String]] = {

    val loadBoards = board.result
    val loadPlayerStates = playerState.result
    val loadFields = field.result

    for {
      boards <- database.run(loadBoards)
      playerStates <- database.run(loadPlayerStates)
      fields <- database.run(loadFields)
    } yield {
      val data = Map(
        "boards" -> Json.toJson(boards),
        "playerStates" -> Json.toJson(playerStates),
        "fields" -> Json.toJson(fields)
      )

      val size = boards.head(1)

      val playerState = playerStates.head(1)

      val cells = fields.map { field =>
        val row = field(3).asInstanceOf[Int]
        val col = field(4).asInstanceOf[Int]
        val cell = field(5).asInstanceOf[String]
        Json.obj("row" -> row, "col" -> col, "cell" -> cell)
      }

      val output = Json.obj(
        "field" -> Json.obj(
          "size" -> size,
          "playerState" -> playerState,
          "cells" -> cells
        )
      )
      
      Some(Json.stringify(output))
    }
  }

  override def closeDatabase(): Unit = {
    database.close()
  }
}
