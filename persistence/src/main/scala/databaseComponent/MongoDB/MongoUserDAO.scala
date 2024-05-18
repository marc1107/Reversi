package databaseComponent.MongoDB

import databaseComponent.UserDAO
import org.bson.json.JsonObject
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

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

  override def delete(): Future[Unit] = {
    Try {
      Await.result(gameCollection.drop().head, 10.seconds)
    } match
      case Failure(exception) =>
        println(exception)
        Future.failed(exception)
      case Success(value) =>
        println(value)
        Future.successful(())
  }
  override def create(): Future[Unit] = {
    Try {
      Await.result(db.createCollection("game").head, 10.seconds)
      Await.result(db.listCollectionNames().head, 10.seconds)
    } match
      case Failure(exception) =>
        println(exception)
        Future.failed(exception)
      case Success(value) =>
        println(value)
        Future.successful(())
  }

  override def save(game: String): Future[Int] = {
    print("save called")
    Future.successful(1)
  }

  override def load(): Future[Option[String]] = ???

  override def closeDatabase(): Unit = {
    client.close()
  }
}
