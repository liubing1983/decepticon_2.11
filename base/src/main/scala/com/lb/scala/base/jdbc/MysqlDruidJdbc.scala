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



  def getConnection(): Option[Connection] = {
    val dataSource: DataSource = DruidDataSourceFactory.createDataSource(pro)
    val ds  = dataSource.getConnection
    Some(ds)
  }

  def closeDataSource(ds : Connection): Unit = {
    ds.close()
  }

}
