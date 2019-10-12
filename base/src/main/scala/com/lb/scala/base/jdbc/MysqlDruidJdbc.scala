package com.lb.scala.base.jdbc

import java.sql.Connection
import java.util.Properties
import javax.sql.DataSource

import com.alibaba.druid.pool.DruidDataSourceFactory

/**
  * @ClassName MysqlDruidJdbc
  * @Description @TODO
  * @Author liubing
  * @Date 2019/8/30 15:08
  * @Version 1.0
  **/
class MysqlDruidJdbc(pro: Properties) {

  val dataSource: DataSource = DruidDataSourceFactory.createDataSource(pro)
  val dao  = dataSource.getConnection

  def getConnection(): Connection = {
    dao
  }

  def closeDataSource(): Unit = {
    dao.close()
  }

}
