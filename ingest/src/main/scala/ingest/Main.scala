package ingest

import akka.actor.ActorSystem
import akka.stream.alpakka.cassandra.CassandraSessionSettings
import akka.stream.alpakka.cassandra.scaladsl.{CassandraSession, CassandraSessionRegistry}
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.LazyLogging
import core.SettingsLoader
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext

object Main extends App with LazyLogging {
  implicit val actorSystem: ActorSystem = ActorSystem("flyer-analytics-ingest")
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val cassandraSession: CassandraSession =
    CassandraSessionRegistry.get(actorSystem).sessionFor(CassandraSessionSettings())

  val settings = SettingsLoader.load[Settings]("flyer-analytics-ingest")
  val source = SourceBuilder.build(settings)
  val flow = FlowBuilder.build

  source.via(flow).to(Sink.ignore).run()
}
