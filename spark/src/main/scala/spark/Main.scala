package spark

import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  val FiveMinutes: Long = 5 * 60 * 1000

  def main(args: Array[String]) {
    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", "127.0.0.1")
      .setAppName("flyer-analytics")

    new SparkContext(conf)
      .cassandraTable("flyer", "events")
      .select("user_id", "timestamp", "flyer_id", "event_type")
      .collect {
        case row if getEventType(row) == "list_flyers" => row
        case row if isValid(row)                       => row
      }
      .groupBy(getUserId)
      .map { case (userId, rows) => (userId, averageViewtime(rows.toList)) }
      .filter(_._2 > 0) // filter out users who generated no meaningful data
      .foreach { case (user, viewtime) => println(s"User $user has an average flyer view time of: $viewtime seconds.") }
  }

  def isValid(row: CassandraRow): Boolean =
    (getEventType(row) == "flyer_open" || getEventType(row) == "item_open") && getFlyerId(row).isDefined

  def getEventType(row: CassandraRow): String = row.get[String]("event_type")
  def getUserId(row: CassandraRow): String = row.get[String]("user_id")
  def getFlyerId(row: CassandraRow): Option[String] = row.get[Option[String]]("flyer_id")
  def getTimestamp(row: CassandraRow): Long = row.get[Long]("timestamp")

  /**
    * A user's viewtime is considered to be the period of time between the user opening the flyer,
    * denoted by a `flyer_open` event with a `flyer_id`, and the use closing the flyer, denoted by a
    * `list_flyers` event with no `flyer_id`. Between these two events, there may be any number of
    * `item_open` events with the same `flyer_id`.
    */
  def averageViewtime(rows: List[CassandraRow]): Long = {
    val timestampsByFlyer = groupTimestampsByFlyer(rows)
    val flatSublists = timestampsByFlyer.flatMap(splitIntoSublists(_, FiveMinutes))
    val viewTimePerSublist = flatSublists.map(timestamps => timestamps.max - timestamps.min).filter(_ > 0)

    if (viewTimePerSublist.nonEmpty)
      (viewTimePerSublist.sum / viewTimePerSublist.size) / 1000
    else
      -1
  }

  /**
    * To calculate the view time for any `flyer_id`, we must consider the `list_flyers` events that may happen
    * in proximity of a `flyer_open` event or a `item_open` event.
    */
  def groupTimestampsByFlyer(rows: List[CassandraRow]): List[List[Long]] = {
    val (openFlyerRows, listFlyerRows) = rows.partition(getFlyerId(_).isDefined)
    openFlyerRows
      .groupBy(getFlyerId)
      .map(_._2.map(getTimestamp) ++ listFlyerRows.map(getTimestamp))
      .toList
  }

  /**
    * Split the input list based into sublists based on the proximity of elements.
    * If two elements are within the given interval, they belong to the same sublist.
    * This process is repeated until all elements are assigned to a sublist.
    */
  def splitIntoSublists(timestamps: List[Long], interval: Long): List[List[Long]] =
    timestamps.sorted.foldLeft(List.empty[List[Long]]) { (intervalLists, timestamp) =>
      if (intervalLists.nonEmpty && isWithinInterval(timestamp, intervalLists.last, interval)) {
        val newLastList = intervalLists.last :+ timestamp
        intervalLists.dropRight(1) :+ newLastList
      } else {
        intervalLists :+ List(timestamp)
      }
    }

  def isWithinInterval(timestamp: Long, timestamps: List[Long], interval: Long): Boolean =
    (timestamp - timestamps.max) < interval

}
