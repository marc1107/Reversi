package databaseComponent.Slick

import databaseComponent.Slick.Tables.{BoardTable, FieldTable, PlayerStateTable}
import databaseComponent.UserDAO
import play.api.libs.json.Json
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class SlickUserDAO extends UserDAO {

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "postgres")
  private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
  private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "postgresdb")
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
      val bo_re = data("boards").as[Seq[(Int, Int)]]
      val pl_re = data("playerStates").as[Seq[(Int, String)]]
      val fi_re = data("fields").as[Seq[(Int, Int, Int, Int, Int, String)]]
      print("bo_re" +   bo_re)
      print("pl_re" +   pl_re)
      print("fi_re" +   fi_re)
      val json = Json.toJson(data)
      print("jons" + json)
      Some(Json.stringify(Json.toJson(data)))
    }
  }

  override def closeDatabase(): Unit = {
    database.close()
  }
}
