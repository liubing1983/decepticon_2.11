package com.lb.scala.devastator


import java.util.concurrent.atomic.LongAdder

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.finupcredit.base.basetools.{ConfigFiles, DBConnectionInfo, MysqlDao}
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

case class FileBean(file: FileStatus, fileSystem: FileSystem, session: String)

case class DireBean(dire: FileStatus, fileSystem: FileSystem, act: ActorRef, session: String)

case object StopBean


class Engine extends Actor with ActorLogging {

  var conf: Configuration = _
  var fileSystem: FileSystem = _

  override def preStart(): Unit = {
    // 1.read configuration information : core-site.xml core-default.xml
    // hdfs-site.xml hdfs-default.xml
    conf = new Configuration()

    fileSystem = FileSystem.get(new Path("/").toUri, conf, "hdfs")


    super.preStart()
  }

  override def postStop(): Unit = {
    fileSystem.close()
    super.postStop()
  }

  override def receive: Receive = {
    case path: String =>

      val file = fileSystem.getFileStatus(new Path(path))
      println(file.getPath)
      println(file.getGroup)
      println(file.isDirectory)
      println(file.isFile)

      // 处理文件
      val fileActor = context.actorOf(Props[BonecrusherFileActor].withRouter(new RoundRobinPool(10)), "bonecrusher-file-Actor")
      // 处理目录
      val direActor = context.actorOf(Props[BonecrusherDireActor].withRouter(new RoundRobinPool(5)), "bonecrusher-dire-Actor")


      file.isFile match {
        case true => fileActor ! FileBean(file, fileSystem, Engine.session)
        case false => direActor ! DireBean(file, fileSystem, fileActor, Engine.session)
      }

    case StopBean => Engine.stopAkkaSystme
  }
}


/**
  * @ClassName Engine
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/7 11:11
  * @Version 1.0
  **/
object Engine {

  val log = LogFactory.getLog(Engine.getClass)

  // 任务计数器
  var refNum: LongAdder = new LongAdder
  // 检测批次
  val session: String = System.currentTimeMillis().toString

  // 加载数据库配置
  val db_pro = new ConfigFiles("jdbc.properties").getProperties().get
  val dao = new MysqlDao(new DBConnectionInfo(db_pro))

  val as = ActorSystem.create("Engine-ActorSystem")
  val actor = as.actorOf(Props[Engine].withRouter(new RoundRobinPool(2)), "Engine-ActorOf")

  def main(args: Array[String]): Unit = {

    actor ! "/"
    refNum.increment()

    // 监控剩余任务
    var flag = true
    while (flag) {
      log.info("目前剩余任务: " + refNum.sum())
      Thread.sleep(3000)
      if (refNum.sum() == 0) {
        actor ! StopBean
        flag = false
      }
    }
  }

  def stopAkkaSystme: Unit = {
    log.info("任务执行完成, 关闭集群!! 执行时间: "+ (System.currentTimeMillis() - session.toLong))
    as.terminate()
    // System.exit(0)
  }

}


