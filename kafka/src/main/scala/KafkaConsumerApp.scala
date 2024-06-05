import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import java.util.{Collections, Properties}

object KafkaConsumerApp extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("group.id", "kafka-consumer-group")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)
  val topic = "output-topic"

  consumer.subscribe(Collections.singletonList(topic))

  while (true) {
    val records = consumer.poll(java.time.Duration.ofMillis(100))
    records.forEach(record => println(s"Consumed record with key ${record.key()} and value ${record.value()}"))
  }
}
