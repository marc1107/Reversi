package api

import akka.http.scaladsl.server.Route
import fieldComponent.{FieldInterface, Stone}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsValue, Json}
import akka.http.scaladsl.server.Directives.*
import fileIoComponent.FileIOInterface
class PersistenceApi(var field: FieldInterface, var fileIO: FileIOInterface) {
  val log: Logger = LoggerFactory.getLogger(getClass)

   val routes: Route = pathPrefix("fileio") {
     pathEnd {
       get {
         log.info("Received GET request for field")
         complete(field.toJsObject.toString)
       } ~
       post {
        log.info("Received POST request for field")
        entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fieldValue: String = (jsonValue \ "field").as[JsValue].toString() //parse json string to JsValue
        field = field.jsonToField(fieldValue)
        fileIO.save(field)
        complete(field.toJsObject.toString())
        }
       }
     } ~
     path("load") {
       get {
        log.info("Received POST request for load")
        val tupel= fileIO.load
        field = tupel(0)
        complete(field.toJsObjectPlayer(tupel(1)).toString())
      }
     }
   }
}