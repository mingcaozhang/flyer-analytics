import com.typesafe.scalalogging.LazyLogging
import config.{Settings, SettingsLoader}
import pureconfig.generic.auto._

object Main extends App with LazyLogging {
  val settings = SettingsLoader.load[Settings]("flyer-analytics-service")
}
