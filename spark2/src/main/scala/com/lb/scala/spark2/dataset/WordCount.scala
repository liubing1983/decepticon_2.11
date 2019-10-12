package com.lb.scala.spark2.dataset

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object WordCountLocal {

  def main(args: Array[String]): Unit = {

   // val conf = new SparkConf().setAppName("wordCount").setMaster("local")

   // val sc = new  SparkContext(conf).getConf
   // sc.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

   //   .registerKryoClasses(Array(WordCountLocal.getClass))

    val spark = SparkSession
      .builder().master("local")
      .appName("SparkSession WordCount Example")
      //.config("spark.sql.warehouse.dir", warehouseLocation)
    // .config(sc)
      .getOrCreate()

    import spark.implicits._

    // val data = spark.read.text("/Users/liubing/Desktop/2222222.txt")

    val data = spark.read.text("/user/hdfs/test.text")

    val aaa = data.map(x => x.toString()).flatMap(x => x.toString().split(" ")).groupByKey(values => values).count()

    aaa.show(200)

    println(data.schema+"-----------")
  }
}
