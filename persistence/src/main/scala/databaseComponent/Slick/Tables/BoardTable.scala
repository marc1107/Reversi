package databaseComponent.Slick.Tables

import slick.jdbc.PostgresProfile.api.*

class BoardTable(tag:Tag) extends Table[(Int,Int)](tag,"board"){
  def boardId = column[Int]("board_id",O.PrimaryKey,O.AutoInc)
  def size = column[Int]("size")
  
  
  def * = (boardId, size)
}
