package com.lb.flink19.demo.source

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import com.lb.flink19.demo.MysqlSourceBean
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

/**
  * @ClassName HiveSource
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/16 11:23
  * @Version 1.0
  **/
class HiveSource extends RichSourceFunction[MysqlSourceBean] {

  var conn: Connection = null
  var ps: PreparedStatement = null
  var rs: ResultSet = null

  /**
    * 创建连接
    *
    * @param parameters
    */
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    conn = getConn()
  }

  /**
    * 关闭连接
    */
  override def close(): Unit = {
    super.close()
    if (rs != null) rs.close()
    if (ps != null) ps.close()
    if (conn != null) conn.close()
  }

  override def cancel(): Unit = {}

  /**
    * 查询数据
    *
    * @param sourceContext
    */
  override def run(sourceContext: SourceFunction.SourceContext[MysqlSourceBean]): Unit = {
    ps = conn.prepareStatement("select  id, guid, name, update_time from zoology_customer order by  update_time desc  limit 5");
    rs = ps.executeQuery()
    while (rs.next()) {
      sourceContext.collect(MysqlSourceBean.apply(rs.getString("id"), rs.getString("guid"), rs.getString("name"), rs.getString("update_time")))
    }
  }


  /**
    * 数据源
    *
    * @return
    */
  def getConn(): Connection = {
    val driverName = "org.apache.hive.jdbc.HiveDriver" //hive驱动名称
    val url = "jdbc:hive2://master:10000/default" //连接hive2服务的连接地址
    val user = "" //对HDFS有操作权限的用户
    val pwd = "" //在非安全模式下，指定一个用户运行查询，忽略密码

    Class.forName(driverName)
    DriverManager.getConnection(url, user, pwd)
  }


}
