package api

import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}
import akka.http.scaladsl.server.Directives.*
import databaseComponent.Slick.SlickUserDAO
import fileIoComponent.FileIOInterface

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class PersistenceApi(var field: FieldInterface, var fileIO: FileIOInterface) {
  val log: Logger = LoggerFactory.getLogger(getClass)

   val routes: Route = pathPrefix("fileio") {
     pathEnd {
       post {
        log.info("Received POST request for field")
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val fieldValue: String = (jsonValue \ "field").as[JsValue].toString() //parse json string to JsValue
          field = field.jsonToField(fieldValue)
          fileIO.save(field)
          val db = SlickUserDAO()

          db.createTables().onComplete {
            case Success(_) =>
              log.info("Tables created")
              db.save(field.toJsObjectPlayer.toString()).onComplete {
                case Success(_) => log.info("Field saved")
                case Failure(exception) => log.error("Field not saved", exception)
              }

            case Failure(exception) => log.error("Tables not created", exception)
          }

          complete(field.toJsObject.toString())
        }
       }
     } ~
     path("load") {
       get {
         log.info("Received POST request for load")
         val tupel= fileIO.load
         val db = SlickUserDAO()

         db.createTables().onComplete {
           case Success(_) =>
             log.info("Tables created")
             db.load().onComplete {
               case Success(fieldOption) =>
                 log.info("Field loaded")
                 val fieldJson = fieldOption.get
                 // TODO: convert returned fieldJson to field (including playerState)
               case Failure(exception) => log.error("Field not loaded", exception)
             }

           case Failure(exception) => log.error("Tables not created", exception)
         }

         db.createTables()
         db.load()
         field = tupel(0)
         complete(field.toJsObjectPlayer.toString())
      }
     }
   }
}