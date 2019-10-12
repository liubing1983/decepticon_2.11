package com.lb.scala.spark2.stream

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object Kafka10Demo {

  def main(args: Array[String]): Unit = {

    val WINDOW_LENGTH = new Duration(20 * 1000)
    val SLIDE_INTERVAL = new Duration(10 * 1000)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "zookeeper.connect" -> "127.0.0.1:2181",
      //"key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "key.deserializer" -> classOf[StringDeserializer].getCanonicalName,
      //"value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> classOf[StringDeserializer].getCanonicalName,
      "group.id" -> "1234",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false"
    )

    //properties.put("bootstrap.servers", "127.0.0.1:9092")
    //properties.put("zookeeper.connect", "127.0.0.1:2181")
    //properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
   // properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    //properties.put("group.id", "test6")

    val topics = Array("lb")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))

    stream.map{record =>
      println(record.key.toString)
      (record.key.toString, record.value.toString)}
      .window(WINDOW_LENGTH, SLIDE_INTERVAL)
      .print()


    ssc.start()             // Start the computation
    ssc.awaitTermination()

  }

}
