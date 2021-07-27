package com.lb.scala.spark2.hudi

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import org.apache.hudi.QuickstartUtils._
import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._

/**
  * @ClassName HudiHw
  * @Description @TODO
  * @Author liubing
  * @Date 2019/12/3 10:57
  * @Version 1.0
  **/
object HudiHw {

  case class Record(id:String, name:String, c:String, d:String, e:String)

  val sparkConf = new SparkConf().setAppName("Hudi hello word Demo")
  //sparkConf.setJars(List(SparkContext.jarOfClass(this.getClass).getOrElse("")))
  sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  //System.setProperty("hadoop.home.dir", "D:\\hadoop\\")
  sparkConf.setMaster("local[2]")

  val ss = SparkSession.builder().config(sparkConf).getOrCreate()

  def main(args: Array[String]): Unit = {

    val tableName = "hudi_cow_table"

    val basePath = "/Users/liubing/demo/hudi/hudi_cow_table"

    import ss.implicits._
    val featureDF = ss.sqlContext.read.textFile("file:////Users/liubing/demo/test.txt")
        .map(_.split(",")).map(x=>Record(x(0), x(1), x(2), x(3), x(4))).toDF

    featureDF.write.format("org.apache.hudi")
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

    /***
      *
      * +-------------------+--------------------+------------------+----------------------+--------------------+---+----+---+---+---+
        |_hoodie_commit_time|_hoodie_commit_seqno|_hoodie_record_key|_hoodie_partition_path|   _hoodie_file_name| id|name|  c|  d|  e|
        +-------------------+--------------------+------------------+----------------------+--------------------+---+----+---+---+---+
        |     20191203134901|  20191203134901_0_1|                 b|               default|b53655ea-7697-485...|  A|   b|  c|  d|  e|
        |     20191203134901|  20191203134901_0_2|                 2|               default|b53655ea-7697-485...|  1|   2|  3|  4|  5|
        +-------------------+--------------------+------------------+----------------------+--------------------+---+----+---+---+---+
      *
      */

  }
}
