package com.lb.scala.spark2.hudi

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import com.lb.scala.spark2.hudi.Jdbc2Hudi.sparkConf
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.hudi.QuickstartUtils._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._

import scala.collection.JavaConversions.mapAsJavaMap

/**
  * 从kafka读取数据, 写入hudi
  *
  * @ClassName Kafka2Hudi
  * @Description @TODO
  * @Author liubing
  * @Date 2019/12/4 17:36
  * @Version 1.0
  **/
object Kafka2Hudi {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      // .setMaster("yarn")
      .setMaster("local[*]")
      .setAppName("Kafka2Hudi")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val ssc = new StreamingContext(conf, Seconds(10))
    val ss = SparkSession.builder().config(sparkConf).config("spark.serializer", "org.apache.spark.serializer.KryoSerializer").getOrCreate()


    // hudi配置
    val tbname = "finup_ecology.test_lb"
    val hdfs_basePath = "/user/liubing/hudi/test_lb"

    // kafka配置
    val kafkaParams = Map[String, String](
      "bootstrap.servers" -> "localhost:9092",
      "zookeeper.connect" -> "127.0.0.1:2181",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "group.id" -> "1234",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false"
    )

    // 从kafka读取数据,  数据格式[1,2,3]
    val stream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](Array("hudi"), kafkaParams))


    // 解析数据
    //    val data = stream.map { x =>
    //      val v = x.value().split(",")
    //      JSON.toJSONString(mapAsJavaMap(Map("name" -> v(0), "age" -> v(1), "sex" -> v(2))), new SerializeConfig(true))
    //    }

    val data = stream.map(_.value().split(",", -1)).filter(_.length == 3).map { v =>
      JSON.toJSONString(mapAsJavaMap(Map("name" -> v(0), "age" -> v(1), "sex" -> v(2))), new SerializeConfig(true))
    }

    data.foreachRDD { x =>
      val df = ss.read.option("timestampFormat", "yyyy-MM-dd HH:mm:ss").json(x)

      // 写入hudi
      df.write.format("org.apache.hudi")
        .options(getQuickstartWriteConfigs)
        .option(RECORDKEY_FIELD_OPT_KEY, "name") // 记录唯一的id
        .option(PRECOMBINE_FIELD_OPT_KEY, "age") // 在数据合并的时候使用到
        .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath") // 分区字段
        .option(TABLE_NAME, tbname)
        .mode(SaveMode.Append)
        .save(hdfs_basePath)
    }

    ssc.start() // Start the computation
    ssc.awaitTermination()
  }
}
