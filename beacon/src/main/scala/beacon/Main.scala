package beacon

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import core.SettingsLoader
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext

object Main extends App with LazyLogging {
  implicit val actorSystem: ActorSystem = ActorSystem("flyer-analytics-beacon")
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  val settings = SettingsLoader.load[Settings]("flyer-analytics-beacon")
  val producer = KafkaProducerBuilder.build(settings)
  val service = FlyerAnalyticsService(settings, producer)
  service.start()
}
