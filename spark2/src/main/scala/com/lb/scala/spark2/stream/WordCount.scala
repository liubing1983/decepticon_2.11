package com.lb.scala.spark2.stream

import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

object WordCount {

  def main(args: Array[String]): Unit = {

    println("=========================================")

    val WINDOW_LENGTH = new Duration(20 * 1000)
    val SLIDE_INTERVAL = new Duration(10 * 1000)


    val conf = new SparkConf()
      //.setMaster("local[2]")
      .setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(1))

    val lines = ssc.socketTextStream("localhost", 9999)

    val windowDStream = lines.map{ x =>
      val a = x.split(",")
      (a(0).toInt, a(1).toInt)
    }// .window(WINDOW_LENGTH, SLIDE_INTERVAL)


    windowDStream.foreachRDD{ rdd =>

      // Get the singleton instance of SparkSession
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._


      // Convert RDD[String] to DataFrame
      val wordsDataFrame = rdd.toDF("a", "b")

      // Create a temporary view
      wordsDataFrame.createOrReplaceTempView("words")


      // Do word count on DataFrame using SQL and print it
      val wordCountsDataFrame = spark.sql("select a, b, (a + b) as sum, max(a, b) from words ")
      wordCountsDataFrame.show()

    }

    ssc.start()             // Start the computation
    ssc.awaitTermination()

  }

}
