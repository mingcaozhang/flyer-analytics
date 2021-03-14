package ingest

import akka.NotUsed
import akka.stream.alpakka.cassandra.CassandraWriteSettings
import akka.stream.alpakka.cassandra.scaladsl.CassandraSession
import akka.stream.alpakka.cassandra.scaladsl.CassandraFlow
import akka.stream.scaladsl.Flow
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import com.datastax.oss.driver.api.core.uuid.Uuids
import core.FlyerEventData

import java.time.{Instant, ZoneId}

object FlowBuilder {

  def build(implicit session: CassandraSession): Flow[FlyerEventData, FlyerEventData, NotUsed] =
    CassandraFlow.create(CassandraWriteSettings.defaults, statement, statementBinder)

  private val statementBinder: (FlyerEventData, PreparedStatement) => BoundStatement =
    (flyerEventData, preparedStatement) =>
      preparedStatement.bind(
        Instant.ofEpochMilli(flyerEventData.timestamp).atZone(ZoneId.of("UTC")).toLocalDate,
        flyerEventData.user_id,
        Long.box(flyerEventData.timestamp),
        Uuids.random(),
        flyerEventData.event_type.toString,
        flyerEventData.flyer_id.orNull,
        flyerEventData.merchant_id.orNull
      )

  private val statement =
    s"INSERT INTO flyer.events(date, user_id, timestamp, id, event_type, flyer_id, merchant_id) VALUES (?, ?, ?, ?, ?, ?, ?)"

}
