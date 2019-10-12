package com.lb.scala.spark2.dataset

import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @ClassName KuduDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2019/9/10 16:06
  * @Version 1.0
  **/
object KuduDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("KuduDemo")
      //设置Master_IP并设置spark参数
      .setMaster("local")
      .set("spark.worker.timeout", "500")
      .set("spark.cores.max", "10")
      .set("spark.rpc.askTimeout", "600s")
      .set("spark.network.timeout", "600s")
      .set("spark.task.maxFailures", "1")
      .set("spark.speculationfalse", "false")
      .set("spark.driver.allowMultipleContexts", "true")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sparkContext = SparkContext.getOrCreate(sparkConf)
    val sqlContext = SparkSession.builder().config(sparkConf).getOrCreate().sqlContext

    //使用spark创建kudu表
    val kuduContext = new KuduContext("cdh1.zxl.com:7051", sqlContext.sparkContext)

    //TODO 1:定义表名
    val kuduTableName = "test_kudu_part"
    val kuduMasters = "cdh1.zxl.com:7051"
    val column = Seq("id", "stage", "type", "rates", "numerator", "denominator", "status", "isalarm", "is_hour", "id_day", "create_time", "update_time")

    // 读取方法1
    val qz_track_rates_config = kuduContext.kuduRDD(sparkContext, kuduTableName, column)
    qz_track_rates_config.foreach(println)


    // 方法2
    val kuduOptions: Map[String, String] = Map(
      "kudu.table" -> kuduTableName,
      "kudu.master" -> kuduMasters)
    val a = sqlContext.read.options(kuduOptions).format("org.apache.kudu.spark.kudu").load()
    println(a.show())


    // 方法3
    sqlContext.read.options(kuduOptions).format("org.apache.kudu.spark.kudu").load().createOrReplaceTempView("test_kudu_part")
    sqlContext.sql("select * from test_kudu_part limit 5").show()
  }


}
