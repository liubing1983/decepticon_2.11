package com.lb.scala.spark2.distributed_datax.plugin

import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @ClassName MysqlSource
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/10 10:26
  * @Version 1.0
  **/
class MysqlSourceLb() {



}

object MysqlSourceLb{

  val connectionProperties = new Properties()
  connectionProperties.put("user", "root")
  connectionProperties.put("password", "tidb@root")
  connectionProperties.put("driver" , "com.mysql.jdbc.Driver")
  // fetchsize：仅适用于read数据。JDBC提取大小，用于确定每次获取的行数。
  // 这可以帮助JDBC驱动程序调优性能，这些驱动程序默认具有较低的提取大小（例如，Oracle每次提取10行）。
  connectionProperties.put("fetchsize" , "5000")

  def mysqlRead(sc : SparkSession): DataFrame={
    sc.read.jdbc("jdbc:mysql://123.59.154.80:3306?useUnicode=true&characterEncoding=UTF-8", "finup_ecology.zoology_dictionary", connectionProperties)
  }
}
