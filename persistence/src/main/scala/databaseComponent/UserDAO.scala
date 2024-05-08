package databaseComponent

import scala.concurrent.Future

trait UserDAO {
  def dropTables(): Future[Unit]
  def createTables(): Future[Unit]
  def save(game: String): Future[Int]
  def load(): Future[String]
  def closeDatabase(): Unit
}
