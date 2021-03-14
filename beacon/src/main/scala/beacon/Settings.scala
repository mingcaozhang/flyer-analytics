package beacon

final case class Settings(host: String, port: Int, kafka: KafkaConfigs)
final case class KafkaConfigs(bootstrapServers: String, topic: String)
