package com.lb.scala.devastator

import java.sql.{Connection, PreparedStatement}

/**
  * @ClassName TidbDao
  * @Description @TODO
  * @Author liubing
  * @Date 2021/1/26 14:54
  * @Version 1.0
  **/
object TidbDao {


  def saveDate(sql: String, conn: Connection){

    val pstm =  conn.createStatement()
    pstm.execute(sql)

  }

}
