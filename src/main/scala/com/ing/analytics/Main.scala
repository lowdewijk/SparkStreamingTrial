import java.io.{InputStreamReader, BufferedReader}
import java.util.Random

import akka.actor._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.ActorHelper
import scala.concurrent.duration._
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.receiver.Receiver
import scala.io.Source


object HackthonApp extends App {

  val conf = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("NetworkWordCount")
    .set("spark.logConf", "true")
    .set("spark.akka.logLifecycleEvents", "true")
  val ssc = new StreamingContext(conf, Seconds(5))

  ssc.start()

  ssc.receiverStream(new StockMarketReceiver(500))

}

class StockMarketReceiver(delayMs : Int)
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) 
  with Logging {

  def onStart() {
    new Thread("Socket Receiver") {
      override def run() { receive() }
    }.start()
  }

  def onStop() {
  }

  private def receive(): Unit = {
    for (line <- Source.fromFile("whatever.csv").getLines()) {
      store(line)
      Thread.sleep(delayMs)
    }
  }
}
