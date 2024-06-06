package kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties

class Producer(val topic: String) extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  def sendValue(key: String, value: String) = {
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record)
  }

  /*for (i <- 1 to 10) {
    val record = new ProducerRecord[String, String](topic, s"key-$i", s"value-$i")
    producer.send(record)
  }

  producer.close()*/
}
