package com.xebia.sst

import java.io.{InputStreamReader, BufferedReader}
import java.util.Random

import akka.actor._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.ActorHelper
import org.joda.time.format.DateTimeFormat
import org.joda.time.{LocalDateTime, DateTime}
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.receiver.Receiver
import scala.io.Source
import org.apache.spark.streaming.StreamingContext._


object HackthonApp extends App {

  val conf = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("HackeDieHack")
    .set("spark.logConf", "true")
    .set("spark.akka.logLifecycleEvents", "true")
  val ssc = new StreamingContext(conf, Milliseconds(200))
  ssc.checkpoint(".")

  val stockMarketStream = ssc.receiverStream(new StockMarketReceiver(100))

  stockMarketStream
    //.window(Milliseconds(30))
    .map(x => (x.date.getYear.toString + x.date.getWeekOfWeekyear.toString, x))
    .updateStateByKey((quotePairs: Seq[Quote], input: Option[Seq[Quote]]) => {
    Some(input.getOrElse(Seq()) ++ quotePairs)
  }).filter { case (key, quotesPerWeek) => quotesPerWeek.size >= 5}
    .map { case (key, quotesPerWeek) => key -> quotesPerWeek.foldLeft(BigDecimal(0))(_ + _.close) / quotesPerWeek.size}
    .print()

  ssc.start()
}

case class Quote(date: DateTime, open: BigDecimal, high: BigDecimal, low: BigDecimal,
                 close: BigDecimal, volume: Long, adjClose: BigDecimal) extends Ordering[Quote] {
  override def compare(x: Quote, y: Quote): Int = 0
}

object Quote {
  def apply(csvLine: String): Quote = {
    val List(rawDate, rawOpen, rawHigh, rawLow, rawClose, rawVolume, rawAdjClose) = csvLine.split(",").toList
    val fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    val dateTime = fmt.parseDateTime(rawDate.toString)
    new Quote(dateTime, BigDecimal(rawOpen), BigDecimal(rawHigh), BigDecimal(rawLow), BigDecimal(rawClose), rawVolume.toLong, BigDecimal(rawAdjClose))
  }
}


class StockMarketReceiver(delayMs: Int)
  extends Receiver[Quote](StorageLevel.MEMORY_AND_DISK_2)
  with Logging {

  def onStart() {
    new Thread("Socket Receiver") {
      override def run() {
        receive()
      }
    }.start()
  }

  def onStop() {
  }

  private def receive(): Unit = {
    val filePath: String = this.getClass.getResource("/nasdaq.csv").getFile
    println(s"PATH================================ $filePath")
    Source.fromFile(filePath).getLines()
      .drop(1)
      .foreach { line =>
      store(Quote(line))
      Thread.sleep(delayMs)
    }
  }
}
