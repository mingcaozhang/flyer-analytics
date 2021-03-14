import sbt._

object Dependencies {

  object Versions {
    val Akka = "2.6.13"
    val AkkaHttp = "10.2.4"
    val AkkaStreamKafka = "2.0.7"
    val AkkaStreamAlpakkaCassandra = "2.0.2"
    val Circe = "0.12.3"
    val Enumeratum = "1.6.1"
    val Kafka = "2.7.0"
    val LogbackClassic = "1.2.3"
    val PureConfig = "0.14.1"
    val ScalaLogging = "3.9.2"
    val Tapir = "0.17.15"

  }

  lazy val deps: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % Versions.LogbackClassic,
    "com.beachape" %% "enumeratum" % Versions.Enumeratum,
    "com.beachape" %% "enumeratum-circe" % Versions.Enumeratum,
    "com.github.pureconfig" %% "pureconfig" % Versions.PureConfig,
    "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % Versions.AkkaStreamAlpakkaCassandra,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Versions.Tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.Tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.Tapir,
    "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
    "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
    "com.typesafe.akka" %% "akka-stream-kafka" % Versions.AkkaStreamKafka,
    "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging,
    "io.circe" %% "circe-core" % Versions.Circe,
    "io.circe" %% "circe-generic" % Versions.Circe,
    "io.circe" %% "circe-parser" % Versions.Circe,
    "org.apache.kafka" %% "kafka" % Versions.Kafka
  )
}
