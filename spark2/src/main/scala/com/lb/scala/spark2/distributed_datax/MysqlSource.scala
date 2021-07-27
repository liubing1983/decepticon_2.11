package com.lb.scala.spark2.distributed_datax

import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @ClassName MysqlSource
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/7 16:17
  * @Version 1.0
  **/
object MysqlSource {

  // 返回datafarme

  def source(sc:SparkSession, pro: Properties) : DataFrame={
    sc.read.jdbc("","",pro)
  }
}
