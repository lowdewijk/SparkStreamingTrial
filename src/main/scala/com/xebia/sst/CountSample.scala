package com.xebia.sst

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Milliseconds, Seconds, StreamingContext}
import org.apache.spark.streaming.StreamingContext._

object CountSample extends App {

  val updateFunc = (values: Seq[Quote], state: Option[Int]) => {
    //val currentCount = values.foldLeft(0)(_ + _)
    val currentCount: Int = values.size
    val previousCount: Int = state.getOrElse(0)

    Some(currentCount + previousCount)
  }

  val sparkConf = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("HackeDieHack")
    .set("spark.logConf", "true")
    .set("spark.akka.logLifecycleEvents", "true")

  // Create the context with a 1 second batch size
  val ssc = new StreamingContext(sparkConf, Seconds(1))
  ssc.checkpoint(".")

  // Create a NetworkInputDStream on target ip:port and count the
  // words in input stream of \n delimited test (eg. generated by 'nc')
  //val lines = ssc.socketTextStream(args(0), args(1).toInt)
  val lines = ssc.receiverStream(new StockMarketReceiver(100))
    .map(x => (1, x))
  //val words = lines.flatMap(_.split(" "))
  //val wordDstream = words.map(x => (x, 1))

  // Update the cumulative count using updateStateByKey
  // This will give a Dstream made of state (which is the cumulative count of the words)
  val stateDstream = lines.updateStateByKey[Int](updateFunc)
  stateDstream.print()
  ssc.start()
  ssc.awaitTermination()
}