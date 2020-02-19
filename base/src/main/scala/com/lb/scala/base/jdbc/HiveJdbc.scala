package com.lb.scala.base.jdbc

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.ResourceBundle
import scala.collection.JavaConversions._
import java.sql.Connection
/**
  * @ClassName HiveJdbc
  * @Description @TODO
  * @Author liubing
  * @Date 2019/10/23 15:34
  * @Version 1.0
  **/
object HiveJdbc {


  val hivedriverClassName = "org.apache.hive.jdbc.HiveDriver"
  val hiveurl= "jdbc:hive2://cdh1:10000"
  val hiveusername = "hive"
  val hivepassword = ""


  def hiveJDBC_RowCount(sql: String) = try {
    Class.forName(hivedriverClassName).newInstance
    val conn = DriverManager.getConnection(hiveurl, hiveusername, hivepassword)
    val pstsm = conn.prepareStatement(sql)

    val resultSet = pstsm.executeQuery

    while(resultSet.next){
      println(resultSet.getString(1))
    }


   //  val rowNum =  if (resultSet.next)  resultSet.getInt(1)  else  0

    // println(rowNum+"----")
  } catch {
    case e: Exception => System.out.println(e)
  }


  def main(args: Array[String]): Unit = {
     hiveJDBC_RowCount("select  ext_user_id  from  risk_assess_request group by  ext_user_id ")
     // hiveJDBC_RowCount("select  *  from  risk_assess_request  ")
  }



}
