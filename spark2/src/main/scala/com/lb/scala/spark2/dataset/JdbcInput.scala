package com.lb.scala.spark2.dataset

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, Row, SparkSession}

object JdbcInput {

  val URL = "jdbc:mysql://123.59.154.94:4000/finup_ecology"
  val DBTABLE_NAME = "finup_lend.lend_customer"
  val USER = "finup_ecology"
  val PASSWORD = "wy613PXTCNB75RiVsrzqZNIZe8YsqBI"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("MoveDataToMySqlTest").setMaster("local[*]")
    val spark = SparkSession.builder.config(conf).getOrCreate

    val jdbcDF: Dataset[Row] = spark.read.format("jdbc")
      .option("url", URL).option("dbtable", DBTABLE_NAME).option("user", USER).option("password", PASSWORD).load
    //jdbcDF.write().parquet(warehouse path);此处可将数据转换成parquet格式文件存入到数据仓库中
    jdbcDF.createOrReplaceTempView("table1")

    // dataset
    val sql: String = "select * from table1  limit 10"
    jdbcDF.show()

    // rdd
    val a = jdbcDF.sqlContext.sql(sql) //.limit(10).foreach(x => println(x.toString()))
    println(a.schema)
    println(a.limit(1).first().toString())
   // jdbcDF.limit(10).foreach( x => println(x.getAs(1)))
   // a.write.jdbc()
    spark.close()
  }
}
