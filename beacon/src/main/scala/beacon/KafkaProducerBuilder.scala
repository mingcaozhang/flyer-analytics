package beacon

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object KafkaProducerBuilder {
  def build(settings: Settings): KafkaProducer[String, String] = {
    val props = new Properties()
    props.put("bootstrap.servers", settings.kafka.bootstrapServers)
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    new KafkaProducer[String, String](props)
  }
}
