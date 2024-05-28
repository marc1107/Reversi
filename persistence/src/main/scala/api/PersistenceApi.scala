package api

import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}
import akka.http.scaladsl.server.Directives.*
import com.google.inject.{Guice, Injector}
import databaseComponent.UserDAO
import fileIoComponent.FileIOInterface
import module.PersistenceModule

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class PersistenceApi(var field: FieldInterface, var fileIO: FileIOInterface) {
  val injector: Injector = Guice.createInjector(new PersistenceModule)
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
          val db = injector.getInstance(classOf[UserDAO])
          db.delete().onComplete {
            case Success(_) =>
              log.info("Tables dropped")
            case Failure(exception) => log.error("Tables not dropped", exception)
          }
          db.create().onComplete {
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
         val db = injector.getInstance(classOf[UserDAO])

         db.create().onComplete {
           case Success(_) =>
             log.info("Tables created")
             db.load().onComplete {
               case Success(fieldOption) =>
                 log.info("Field loaded")
                 val json = Json.parse(fieldOption.get)

                 val fields: String = (json \ "field").as[JsValue].toString()

                 println(fields)
                 field = field.jsonToField(fields)


               case Failure(exception) => log.error("Field not loaded", exception)
             }

           case Failure(exception) => log.error("Tables not created", exception)
         }

         field = tupel(0)
         complete(field.toJsObjectPlayer.toString())
      }
     }
   }
}