package databaseComponent.Slick.Tables

import slick.jdbc.PostgresProfile.api.*

class FieldTable(tag: Tag) extends Table[(Int, Int, Int, Int, Int, String)](tag, "field") {
  def fieldId = column[Int]("fieldId", O.PrimaryKey, O.AutoInc)
  def boardId = column[Int]("boardId")
  def playerId = column[Int]("playerId")
  def r = column[Int]("r")
  def c = column[Int]("c")
  def stone = column[String]("stone")
  
  def * = (fieldId, boardId, playerId, r, c, stone)
  
  def board = foreignKey("board_fk", boardId, boardTable)(_.boardId)
  def player = foreignKey("player_fk", playerId, playerStateTable)(_.playerStateId)
  
  def boardTable = TableQuery[BoardTable]
  def playerStateTable = TableQuery[PlayerStateTable]
}
