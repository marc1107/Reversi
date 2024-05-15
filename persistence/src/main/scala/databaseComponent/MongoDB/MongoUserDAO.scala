package databaseComponent.MongoDB

import databaseComponent.UserDAO
import org.bson.json.JsonObject
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Future

class MongoUserDAO extends UserDAO {
  private val databaseDB: String = sys.env.getOrElse("MONGO_DB", "mongo")
  private val databaseUser: String =
    sys.env.getOrElse("MONGO_USERNAME", "root")
  private val databasePassword: String =
    sys.env.getOrElse("MONGO_PASSWORD", "mongo")
  private val databasePort: String = sys.env.getOrElse("MONGO_PORT", "27017")
  private val databaseHost: String =
    sys.env.getOrElse("MONGO_HOST", "mongodb")

  private val databaseURI: String =
    s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin"
  private val client: MongoClient = MongoClient(databaseURI)
  private val db: MongoDatabase = client.getDatabase(databaseDB)
  private val gameCollection: MongoCollection[JsonObject] =
    db.getCollection("game")

  override def dropTables(): Future[Unit] = ???
  override def createTables(): Future[Unit] = ???
  override def save(game: String): Future[Int] = ???
  override def load(): Future[Option[String]] = ???
  override def closeDatabase(): Unit = ???
}
