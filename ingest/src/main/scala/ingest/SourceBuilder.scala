package ingest

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.Control
import akka.stream.scaladsl.{Sink, Source}
import core.FlyerEventData
import io.circe.{Json, parser}
import io.circe.generic.auto._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

object SourceBuilder {

  /**
    * For simplicity's sake, we throw away errors. To really productionize this code, we could use
    * a dead letter queue or some other error handling mechanism.
    */
  def build(
    settings: Settings
  )(implicit actorSystem: ActorSystem): Source[FlyerEventData, Control] =
    Consumer
      .committableSource(
        consumerSettings(settings.kafka.bootstrapServers, settings.kafka.consumerGroup),
        Subscriptions.topics(settings.kafka.topic)
      )
      .map(msg => parser.parse(msg.record.value()).getOrElse(Json.Null))
      .map(_.as[FlyerEventData])
      .divertTo(Sink.ignore, _.isLeft)
      .map(_.toOption.get)

  private def consumerSettings(bootstrapServer: String, consumerGroup: String)(implicit
    actorSystem: ActorSystem
  ): ConsumerSettings[String, String] = {
    ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServer)
      .withGroupId(consumerGroup)
      .withProperty(
        ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        "org.apache.kafka.clients.consumer.RoundRobinAssignor"
      )
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
  }

}
