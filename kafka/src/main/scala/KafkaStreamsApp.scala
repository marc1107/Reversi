import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.{KStream, Produced}
import java.util.Properties

object KafkaStreamsApp extends App {
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde")
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde")

  val builder = new StreamsBuilder()
  val inputTopic = "input-topic"
  val outputTopic = "output-topic"

  val stream: KStream[String, String] = builder.stream(inputTopic)
  val transformedStream = stream.mapValues(value => s"processed_$value")
  transformedStream.to(outputTopic, Produced.`with`(org.apache.kafka.common.serialization.Serdes.String, org.apache.kafka.common.serialization.Serdes.String))

  val streams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close()
  }
}
