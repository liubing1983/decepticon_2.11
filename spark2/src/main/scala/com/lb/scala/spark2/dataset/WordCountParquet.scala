package com.lb.scala.spark2.dataset

import org.apache.spark.sql.{SQLContext, SparkSession}

object WordCountParquet {
  // spark-shell --master  yarn  --driver-memory 2G  --driver-cores 2  --executor-memory  4G    --total-executor-cores 10  --name spark-shell-liubing

  val spark = SparkSession.builder()
    .appName("SparkSession WordCount Example")
    //.config("spark.sql.warehouse.dir", warehouseLocation)
    //.config(sc)
    .getOrCreate()

  val sc = spark.sparkContext.getConf

  sc.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

  sc.registerKryoClasses(Array(WordCountParquet.getClass))

  import spark.implicits._

    val data = spark.read.parquet("/opt/base/all_user_profile/*.parquet").createOrReplaceTempView("hehe")

  val aa = spark.sqlContext.sql("select * from hehe")


   aa.map(x => x.toString()).flatMap(x => x.toString().split(" ")).groupByKey(values => values).count()

  println(aa.schema+"-----------")

}
