package databaseComponent.Slick.Tables

import slick.jdbc.PostgresProfile.api.*

class PlayerStateTable(tag: Tag) extends Table[(Int, String)](tag, "playerState") {
  def playerStateId = column[Int]("playerStateId", O.PrimaryKey, O.AutoInc)
  def playerState = column[String]("playerState")

  def * = (playerStateId, playerState)
}
