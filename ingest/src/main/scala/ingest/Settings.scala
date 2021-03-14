package ingest

final case class Settings(kafka: KafkaConfigs)
final case class KafkaConfigs(bootstrapServers: String, consumerGroup: String, topic: String)
