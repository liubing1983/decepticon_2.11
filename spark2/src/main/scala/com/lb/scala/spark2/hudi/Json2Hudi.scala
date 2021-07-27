package com.lb.scala.spark2.hudi

import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}


/**
  * @ClassName Json2Hudi
  * @Description @TODO
  * @Author liubing
  * @Date 2020/8/12 10:08
  * @Version 1.0
  **/
object Json2Hudi {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("Kafka2Hudi")

    val spark = SparkSession.builder().config(conf)
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer").getOrCreate()

    val tbname = "test_hudi"
    val basePath = "file:////Users/liubing/demo/hudi/test_hudi"
    val hdfs_basePath = "/user/liubing/hudi/test_hudi"

    val df = spark.sqlContext.read
      .option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
      //.json(args(0)) .toDF("name", "age", "sex")
      .json("file:////Users/liubing/IdeaProjects/decepticon_2.11/spark2/src/main/resources/demo.json")
      .toDF("name", "age", "sex")

    df.show()

    df.write.format("org.apache.hudi")
      .options(getQuickstartWriteConfigs)
      //.option("hoodie.upsert.shuffle.parallelism", "1")
      //.option(HIVE_URL_OPT_KEY, "jdbc:hive2://cdh1:10000")
      //.option(HIVE_USER_OPT_KEY, "aaaa")
      //.option(HIVE_PASS_OPT_KEY, "123456")
      //.option(HIVE_DATABASE_OPT_KEY, "test_dc")
      //.option(HIVE_SYNC_ENABLED_OPT_KEY, true)
      //.option(HIVE_TABLE_OPT_KEY, tbname)
      //.option(HIVE_PARTITION_FIELDS_OPT_KEY, "partiton")
      //.option(HIVE_PARTITION_EXTRACTOR_CLASS_OPT_KEY, "com.xxx.dc.MyPartitionValueExtractor")
      .option(PRECOMBINE_FIELD_OPT_KEY, "name")
      .option(RECORDKEY_FIELD_OPT_KEY, "age") // 记录唯一的id
      .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath")
      .option(TABLE_NAME, tbname)
      .mode(SaveMode.Overwrite)
      .save(hdfs_basePath)
  }
}

case class Demo(name: String, age: Int, sex: String)