package com.lb.scala.spark2.dataset

import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.rdd.EsSpark

object SparkSession2Es {

  case class Trip(k: String, v: String)
  def main(args: Array[String]): Unit = {


    println("Hello World!")
   // System.setProperty("hadoop.home.dir", "G:\\hadoop_home")

    val sparkSession = SparkSession.builder()
      .appName("SparkSession2Es")
      .master("local[2]")
      .config("es.index.auto.create", "true")
      .config("pushdown", "true")
      .config("es.nodes", "127.0.0.1")
      .config("es.port", "9200")
      .config("es.nodes.wan.only", "true")
      .getOrCreate()



// 写入数据
    val data = sparkSession.read.text("/Users/liubing/analysis-day.log")
    val rdd = data.rdd.map{  x => Trip(x.toString(), "haha") }
    EsSpark.saveToEs(rdd, "abc/test")


    //  读取数据
    val sparkDF = sparkSession.sqlContext.read.format("org.elasticsearch.spark.sql").load("abc/test")
   // sparkDF.take(1000).foreach(println(_))
   // println(sparkDF.count())

    sparkDF.columns.foreach(println)

    sparkDF
      //.limit(10)
      .foreach(x => println(x.toString()))


    sparkSession.stop()

  }

}
