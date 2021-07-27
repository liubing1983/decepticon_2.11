package com.lb.scala.spark2.hudi

import java.util.Properties

import org.apache.hudi.DataSourceWriteOptions.{PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * @ClassName Jdbc2Hudi
  * @Description @TODO
  * @Author liubing
  * @Date 2020/8/13 14:29
  * @Version 1.0
  **/
object Jdbc2Hudi {

  // 数据库配置
  val properties = new Properties()
  properties.put("user", "root")
  properties.put("password", "tidb@root")
  properties.put("driver", "com.mysql.jdbc.Driver")
  // fetchsize：仅适用于read数据。JDBC提取大小，用于确定每次获取的行数。
  // 这可以帮助JDBC驱动程序调优性能，这些驱动程序默认具有较低的提取大小（例如，Oracle每次提取10行）。
  properties.put("fetchsize", "5000")

  val url = s"""jdbc:mysql://123.59.154.80:3306/finup_ecology?user=root&password=tidb@root&useUnicode=true&characterEncoding=UTF-8"""
  val dbtable = s"""finup_ecology.zoology_dictionary"""

  // spark配置
  val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Kafka2Hudi")
  val ss = SparkSession.builder().config(sparkConf).config("spark.serializer", "org.apache.spark.serializer.KryoSerializer").getOrCreate()

  // hudi配置
  val tbname = "finup_ecology.zoology_dictionary"
  val hdfs_basePath = "/user/liubing/hudi/finup_ecology"

  def main(args: Array[String]): Unit = {
     // write
     read
  }

  def write: Unit ={
    // 注册表
    ss.sqlContext.read
      .option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
      .jdbc(url, dbtable, properties).createTempView("zoology_dictionary")

    // 查询数据, 并添加分区数据
    val df = ss.sqlContext.sql(" select *, '20200814' from zoology_dictionary ")
      .toDF("id", "type", "type_name", "dict_code", "dict_name",
        "finup_database", "finup_table", "finup_column", "parent_id", "create_time",
        "update_time", "partitionpath" )

    df.show(100)

    // 写入hudi
    df.write.format("org.apache.hudi")
      .options(getQuickstartWriteConfigs)
      .option(RECORDKEY_FIELD_OPT_KEY, "id") // 记录唯一的id
      .option(PRECOMBINE_FIELD_OPT_KEY, "update_time") // 在数据合并的时候使用到
      .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath") // 分区字段
      .option(TABLE_NAME, tbname)
      .mode(SaveMode.Append)
      .save(hdfs_basePath)
  }

  def read: Unit ={
    val hudi_df = ss.sqlContext.read.format("org.apache.hudi").load(hdfs_basePath+"/*").createTempView("zoology_dictionary_hudi")
    ss.sqlContext.sql("select * from zoology_dictionary_hudi where id = 30105").show
  }

}