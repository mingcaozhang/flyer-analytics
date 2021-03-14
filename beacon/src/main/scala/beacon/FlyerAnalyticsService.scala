package beacon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging
import core.{FlyerEvent, FlyerEventData}
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.{AkkaHttpServerInterpreter, AkkaHttpServerOptions}
import sttp.tapir.server.{DecodeFailureHandling, ServerDefaults}

import scala.concurrent.{ExecutionContext, Future}

class FlyerAnalyticsService(host: String, port: Int, topic: String, kafkaProducer: KafkaProducer[String, String])(
  implicit
  actorSystem: ActorSystem,
  ec: ExecutionContext
) extends LazyLogging {

  /**
    * Any Json decoding failure should return a 400.
    */
  implicit val serverOptions: AkkaHttpServerOptions = AkkaHttpServerOptions.default.copy(
    decodeFailureHandler = ServerDefaults.decodeFailureHandler.copy(
      response = { (response, message) =>
        DecodeFailureHandling.response(ServerDefaults.failureOutput(jsonBody[Error]))(response, Error(400, message))
      }
    )
  )

  private val flyerEndpoint = endpoint.post.in("beacon" / "collect").in(jsonBody[FlyerEvent]).errorOut(jsonBody[Error])

  /**
    * For simplicity's sake, we are operating under the assumption that the requester does not care about the
    * response if the request was successful, and only needs to be alerted of any failures.
    */
  private def handler(flyerEvent: FlyerEvent): Future[Either[Error, Unit]] =
    Future(
      kafkaProducer
        .send(new ProducerRecord[String, String](topic, FlyerEventData(flyerEvent).asJson.deepDropNullValues.noSpaces))
        .get
    ).map(_ => Right(()))

  def start(): Unit =
    Http().newServerAt(host, port).bindFlow(AkkaHttpServerInterpreter.toRoute(flyerEndpoint)(handler)).onComplete { _ =>
      logger.info(s"Server listening for requests at $host:$port")
    }
}

object FlyerAnalyticsService {
  def apply(
    settings: Settings,
    kafkaProducer: KafkaProducer[String, String]
  )(implicit actorSystem: ActorSystem, ec: ExecutionContext): FlyerAnalyticsService =
    new FlyerAnalyticsService(settings.host, settings.port, settings.kafka.topic, kafkaProducer)
}
