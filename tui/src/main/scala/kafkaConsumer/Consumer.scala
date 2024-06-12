package kafkaConsumer

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import fieldComponent.{Field, FieldInterface, Stone}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContextExecutor

class Consumer(val topic: String, val updateUI: (field: FieldInterface, playerState: Stone) => Unit) extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val bootstrapServers = "localhost:9092"

  val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("kafka-consumer-group-tui")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  var field: FieldInterface = new Field(0, Stone.Empty)
  var playerState: Stone = Stone.Empty

  Consumer
    .plainSource(consumerSettings, Subscriptions.topics(topic))
    .map(record => (record.key, record.value))
    .to(Sink.foreach { case (key, value) =>
      key match {
        case "size" => field = new Field(value.toInt, Stone.Empty)
        case "playerState" => playerState = value match {
          case "□" => Stone.W
          case "■" => Stone.B
          case _ => Stone.Empty
        }
        case "end" => updateUI(field, playerState)
        case _ => // the key is in the format "i-j"
          val coordinates = key.split("-").map(_.toInt)
          val stone = value match {
            case "□" => Stone.W
            case "■" => Stone.B
            case _ => Stone.Empty
          }
          field = field.put(stone, coordinates(0), coordinates(1))
      }
    })
    .run()
}
