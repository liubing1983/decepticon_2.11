package com.lb.scala.akka.jdbc

import java.sql.{Connection, DriverManager}

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.RoundRobinPool


class MysqlConn{

  //初始化驱动类com.mysql.jdbc.Driver
  Class.forName("com.mysql.jdbc.Driver")
   val conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lb?characterEncoding=UTF-8","root", "123456")

  def getConnection(): Connection = {
    conn
  }

}

/**
  * @ClassName MysqlJdbc
  * @Description @TODO
  * @Author liubing
  * @Date 2019/9/3 11:59
  * @Version 1.0
  **/
class MysqlJdbc  extends  Actor with ActorLogging{

  var conn: Connection = null


  override def preStart(): Unit = {
    // 创建actor时, 初始化 mysql  connection

      //初始化驱动类com.mysql.jdbc.Driver
//      Class.forName("com.mysql.jdbc.Driver")
//    if(conn == null){
//      conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lb?characterEncoding=UTF-8","root", "123456")
//    }

    // 单例, 所有线程使用一个连接
    conn = MysqlJdbc.mysqlinit.getConnection()

    super.preStart()
  }

  override def postStop(): Unit = {
    // 关闭连接
    if(conn != null) conn.close()
    super.postStop()
  }

  override def receive: Receive = {
    case "insert" => println("123")
    case "update" =>
    case "del" =>
  }
}


object  MysqlJdbc{

  val sys = ActorSystem.create("MysqlJdbc_system")
  val ref  = sys.actorOf(Props[MysqlJdbc].withRouter(new RoundRobinPool(100)), "MysqlJdbc_actorRef")
  val mysqlinit  = new MysqlConn
  def main(args: Array[String]): Unit = {
    ref ! "insert"

  }

}