package com.lb.scala.spark2.distributed_datax.plugin

import java.util.Properties

import org.apache.spark.sql.{DataFrame, SaveMode}

/**
  * @ClassName MysqlSinkLb
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/15 10:03
  * @Version 1.0
  **/
class MysqlSinkLb() {

}

object  MysqlSinkLb{

  val connectionProperties = new Properties()
  connectionProperties.put("user", "root")
  connectionProperties.put("password", "tidb@root")
  connectionProperties.put("driver" , "com.mysql.jdbc.Driver")
  // fetchsize：仅适用于read数据。JDBC提取大小，用于确定每次获取的行数。
  // 这可以帮助JDBC驱动程序调优性能，这些驱动程序默认具有较低的提取大小（例如，Oracle每次提取10行）。
  connectionProperties.put("fetchsize" , "5000")

  def mysqlWrite(df:DataFrame, mode: String) ={

    val saveMode =   mode match {
      case "overwrite" => SaveMode.Overwrite                                // 如果数据/表已经存在，则预期现有数据将被DataFrame的内容覆盖。
      case "append" => SaveMode.Append                                      // 如果数据/表已存在，则DataFrame的内容应附加到现有数据。
      case "ignore" => SaveMode.Ignore                                      // 如果数据已存在，则预期保存操作不会保存DataFrame的内容并且不会更改现有数据。 这类似于SQL中的CREATE TABLE IF NOT EXISTS。
      case "error" | "errorifexists" | "default" => SaveMode.ErrorIfExists  // 将DataFrame保存到数据源时，如果数据已存在，则会引发异常。
      case _ => SaveMode.ErrorIfExists
    }

    df.write.mode(saveMode).jdbc("jdbc:mysql://123.59.154.80:3306?useUnicode=true&characterEncoding=UTF-8", "finup_ecology.zoology_dictionary_2", connectionProperties)
  }

}
