package com.lb.flink19.demo.source

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import com.lb.flink19.demo.MysqlSourceBean
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}


/**
  * @ClassName MysqlSource
  * @Description @TODO
  * @Author liu bing
  * @Date 2020/3/5 14:31
  * @Version 1.0
  **/
class MysqlSource extends RichSourceFunction[MysqlSourceBean]{

  var conn : Connection = null
  var ps : PreparedStatement = null
  var rs : ResultSet = null

  /**
    * 创建连接
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
    if(rs != null) rs.close()
    if(ps != null) ps.close()
    if(conn != null) conn.close()
  }

  override def cancel(): Unit = {}

  /**
    * 查询数据
    * @param sourceContext
    */
  override def run(sourceContext: SourceFunction.SourceContext[MysqlSourceBean]): Unit = {
    ps  = conn.prepareStatement("select  id, guid, name, update_time from zoology_customer order by  update_time desc  limit 5");
    rs = ps.executeQuery()
    while(rs.next()){
      sourceContext.collect(MysqlSourceBean.apply(rs.getString("id"),rs.getString("guid"),rs.getString("name"),rs.getString("update_time")))
    }
  }


  /**
    * 数据源
    * @return
    */
  def  getConn() : Connection ={
    val url= "jdbc:mysql://123.59.154.80:3306/finup_ecology?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"
    val user = "finup_ecology"
    val pwd = "finup_ecology!@#"

    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection(url, user, pwd)
  }
}
