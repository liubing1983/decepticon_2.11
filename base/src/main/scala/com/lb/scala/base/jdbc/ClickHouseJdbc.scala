package com.lb.scala.base.jdbc

import java.sql.{Connection, DriverManager, Statement}


/**
  * @ClassName ClickHouseJdbc
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/30 09:53
  * @Version 1.0
  **/
object ClickHouseJdbc {
  def main(args: Array[String]): Unit = {
    Class.forName("ru.yandex.clickhouse.ClickHouseDriver")
    //连接
    val connection: Connection = DriverManager.getConnection("jdbc:clickhouse://cdh3:8123")
    val statement: Statement = connection.createStatement()
    //建立查询
    val sql = "show databases;"
    var res1 = statement.executeQuery(sql)
    //获取结果
    while (res1.next()) {
      val result = res1.getString(1)
      println(result)
    }
  }
}



