package com.lb.scala.spark2.stream

import java.text.SimpleDateFormat
import java.time.LocalDateTime

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import com.lb.scala.spark2.stream.udf._
import org.elasticsearch.spark.sql.EsSparkSQL

case class Abc(a: String, b: String)

/**
  * 使用
  */
object Kafka10SqlDemo {

  @volatile
  private var instance: Broadcast[Map[String, String]] = null

  def getInstance(sc: SparkContext): Broadcast[Map[String, String]] = {
    if (instance == null) {
      synchronized {
        if (instance == null) {
          val wordBlacklist = Map("sql" -> "select a, b, testAbc(a), testUdf(b) from words", //a, b, (a + b)
            "column" -> "a,b")
          instance = sc.broadcast(wordBlacklist)
        }
      }
    }
    instance
  }


  def main(args: Array[String]): Unit = {

    val WINDOW_LENGTH = new Duration(20 * 1000)
    val SLIDE_INTERVAL = new Duration(10 * 1000)

    val conf = new SparkConf().setMaster("local[3]").setAppName("NetworkWordCount")

    conf.set("es.index.auto.create", "true")
    conf.set("es.nodes", "127.0.0.1")
    conf.set("es.port", "9200")

    val ssc = new StreamingContext(conf, Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "zookeeper.connect" -> "127.0.0.1:2181",
      "key.deserializer" -> classOf[StringDeserializer].getCanonicalName,
      "value.deserializer" -> classOf[StringDeserializer].getCanonicalName,
      "group.id" -> "1234",
      "auto.offset.reset" -> "latest",  // latest   earliest
      "enable.auto.commit" -> "false"
    )

    // properties.put("bootstrap.servers", "127.0.0.1:9092")
    // properties.put("zookeeper.connect", "127.0.0.1:2181")
    // properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    // properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    // properties.put("group.id", "test6")

    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val start: Long = sdf.parse("2019-03-06 00:00:00").getTime()

    println(start+"---------------------------------------")

    val topicPartition0 = new TopicPartition("lb", 0)

    val map = Map(topicPartition0 -> start)

    val topics = Array("lb, lb1")
    // : InputDStream[ConsumerRecord[String, String]]
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))

// : DStream[Array[String] ]
    val windowDStream = stream.map{record =>
      println(record.value+"-------"+record.timestamp()+"----"+record.offset()+"----"+ record.partition())

      val s = record.value.toString.split("\\,")
      Abc.apply(s(0), s(1))
    }.window(WINDOW_LENGTH, SLIDE_INTERVAL)

    windowDStream.foreachRDD{ rdd =>

      // Get the singleton instance of SparkSession
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._

      val m = getInstance(rdd.sparkContext)

      // Convert RDD[String] to DataFrame

      import org.apache.spark.sql.types._
      val schema = StructType(List(
        StructField("integer_column", IntegerType, nullable = false),
        StructField("string_column", StringType, nullable = true),
        StructField("date_column", DateType, nullable = true)
      ))

      // val wordsDataFrame = rdd.toDF(m.value.getOrElse("column", "").toString.split(",") : _*)
      val wordsDataFrame = rdd.toDF()

      // Create a temporary view
      wordsDataFrame.createOrReplaceTempView("words")

      // 通过匿名函数注册udf
      spark.udf.register("testUdf", (s:String) => s.length+10+"")

      // 通过方法名注册udf函数
      spark.udf.register("testAbc", TestUdf.testAbc _)

      // Do word count on DataFrame using SQL and print it
      val wordCountsDataFrame = spark.sql(m.value.getOrElse("sql", ""))
      //wordCountsDataFrame.show()

       val aaa = wordCountsDataFrame.map{line =>

          Map("a" -> line.getString(0), "b" -> line.getString(1),  "a_length" -> line.getString(2), "b_length" -> line.getString(3), "time" -> LocalDateTime.now())
        }

      EsSparkSQL.saveToEs(aaa, "/ab/aaa/")
    }

    ssc.start()
    ssc.awaitTermination()

  }

}

