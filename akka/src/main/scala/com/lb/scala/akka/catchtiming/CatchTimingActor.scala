package com.lb.scala.akka.catchtiming

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.finupcredit.base.basetools.{ConfigFiles, DBConnectionInfo, MysqlDao}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * @ClassName CatchTimingActor
  * @Description @TODO
  * @Author liubing
  * @Date 2021/7/27 16:36
  * @Version 1.0
  **/
class CatchTimingActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ =>
  }
}

object CatchTimingActor extends App {

  val log = LoggerFactory.getLogger(CatchTimingActor.getClass)

  val sys = ActorSystem.create("catch-timing-Actor")
  val ref = sys.actorOf(Props[CatchTimingActor].withRouter(new RoundRobinPool(10)), "catch-timing-ref")

  // 从数据库中获取数据
  // 加载数据库配置
  val db_pro = new ConfigFiles("jdbc.properties").getProperties().get
  val dao = new MysqlDao(new DBConnectionInfo(db_pro))

  while (true) {

    dao.getDataList(db_pro.getProperty("getProjectSql"), db_pro.getProperty("getProjectSql_column"), false) match {
      case None => log.error("获取任务信息失败!!!")
      case Some(list) =>
        list.size() match {
          case 0 => log.warn("数据库中没有有效的任务!!!")
          case _ => abc(list)
        }
    }

    // 循环监控时间
    Thread.sleep(1000 * 30)

  }

  private def abc(list: java.util.ArrayList[Map[String, String]]) {
    list.foreach { x =>
      val status = x.getOrElse("status", "0").toInt
      val name = x.get("name")
      val id = x.get("id")
      if (status == 0) {
        log.info(s"任务${name}当前时间暂停!!!!+ ${ref.path}")
        // 查找akka中对应的任务
        sys.actorSelection("")
sys.dispatcher

      } else {
        log.info(s"任务${name}当前时间[${}]启动!!!!")
        // 查找akka中对应的任务是否在启动
      }
    }
  }
}