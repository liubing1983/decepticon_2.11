package com.lb.scala.spark2.hudi

import com.lb.scala.spark2.hudi.HudiHw.{Record, ss}
import org.apache.hudi.DataSourceWriteOptions.{PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SaveMode.Overwrite
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/**
  * @ClassName Kafka2Hudi
  * @Description @TODO
  * @Author liubing
  * @Date 2019/12/4 17:36
  * @Version 1.0
  **/
object Kafka2Hudi {

  def main(args: Array[String]): Unit = {



    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(10))

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

    stream.foreachRDD{rdd =>
      val data = rdd.map{ x => x.value().toString.split(",").map(x => (x(0), x(1), x(2), x(3), x(4)))}
      data.write
    }

    stream.map{record =>
      record.value().toString.split(",").map(x => (x(0), x(1), x(2), x(3), x(4)))


    .write.format("org.apache.hudi")
      .options(getQuickstartWriteConfigs)
      .option(PRECOMBINE_FIELD_OPT_KEY, "id")
      .option(RECORDKEY_FIELD_OPT_KEY, "name")
      .option(TABLE_NAME, tableName)
      .mode(Overwrite)
      .save(basePath)

    val toViewDF = ss.sqlContext.read.format("org.apache.hudi").load(basePath + "/*/*")

    toViewDF.show

    toViewDF.createOrReplaceTempView("huditb")

    ss.sql("select * from huditb ").show




    ssc.start()             // Start the computation
    ssc.awaitTermination()

  }


}
