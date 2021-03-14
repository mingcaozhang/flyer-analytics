import java.io.{BufferedWriter, File, FileWriter}
import java.time.ZonedDateTime
import scala.io.Source

def writeFile(filename: String, lines: Seq[String]): Unit = {
  val file = new File(filename)
  val bw = new BufferedWriter(new FileWriter(file))
  for (line <- lines) {
    bw.write(s"$line\n")
  }
  bw.close()
}

def readFile(fileName: String): List[String] = {
  val source = Source.fromFile(fileName)
  val lines = source.getLines().toList
  source.close()
  lines
}

def wrapQuotes(str: String): String = s"\'$str\'"

def emptyToNull(str: String): String = {
  str match {
    case "" => "null"
    case _ => wrapQuotes(str)
  }
}

def csvToList(str: String): List[String] = str.split(",", -1).toList

val inserts = readFile("dataset.csv").drop(1).map(csvToList).flatMap {
  case List(zdt, userId, event, flyerId, merchantId) =>
    val zonedDateTime = ZonedDateTime.parse(zdt)
    val date = zonedDateTime.toLocalDate
    val timestamp = zonedDateTime.toInstant.toEpochMilli
    Some(s"""INSERT INTO flyer.events(date,user_id,timestamp,id,event_type,flyer_id,merchant_id) VALUES(${wrapQuotes(date.toString)},${wrapQuotes(userId)},$timestamp,uuid(),${wrapQuotes(event)},${emptyToNull(flyerId)},${emptyToNull(merchantId)});""")
  case _ => None
}

writeFile("insert.cql", inserts)

