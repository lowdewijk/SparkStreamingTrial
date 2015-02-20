import java.util.Random

import akka.actor._
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.ActorHelper
import scala.concurrent.duration._
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._


object HackthonApp extends App {

  val conf = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("NetworkWordCount")
    .set("spark.logConf", "true")
    .set("spark.akka.logLifecycleEvents", "true")
  val ssc = new StreamingContext(conf, Seconds(5))

  ssc.start()

}

