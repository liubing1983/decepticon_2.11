package com.lb.scala.devastator

import akka.actor.{Actor, ActorLogging, Props}
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

/**
  * 处理目录
  *
  * @ClassName BonecrusherDireActor
  * @Description @TODO
  * @Author liubing
  * @Date 2020/12/22 16:50
  * @Version 1.0
  **/
class BonecrusherDireActor extends Actor with ActorLogging {

  // 过滤的目录
  val filterSeq = Seq(".Trash", ".sparkStaging", ".staging", "history", "tmp", ".tmp", ".temp", ".aux")


  override def preStart(): Unit = {
    super.preStart()
  }

  override def postStop(): Unit = {
    //fileSystem.close()
    super.postStop()
  }


  // 业务逻辑
  override def receive: Receive = {
    case d: DireBean =>
      // 存储当前文件夹信息
      saveDateBo(d.dire)

      // 遍历文件夹
      d.fileSystem.listStatus(d.dire.getPath).foreach { x =>
        //  判断是否为文件
        x.isFile match {
          // 文件
          case true =>
            d.act ! FileBean.apply(x, d.fileSystem, d.session)
            Engine.refNum.increment()
          // 文件夹, 过滤目录
          case false if (!filterSeq.exists(f => f.equals(x.getPath.getName))) =>
            // println(x.getPath.getName)
            self ! DireBean(x, d.fileSystem, d.act, d.session)
            Engine.refNum.increment()
          case _ =>
        }
      }

      Engine.refNum.decrement()
  }

  private def saveDateBo(dire: FileStatus): Option[Int] = {


    // 所属集群id, 全路径, 目录名称, 权限, 层级, 检查批次号, 保存时间
    val sql = if (StringUtils.isBlank(dire.getPath.getName)) {
      s"""
         | insert into finup_ecology.hdfs_system_directory (sys_id, path, directory, authority, tier, session, create_time) values
         | (1, '${dire.getPath}', '/','${dire.getPermission.getGroupAction.toString}',
         |  0, ${Engine.session} , now())
         |
       """.stripMargin
    } else
      s"""
         | insert into finup_ecology.hdfs_system_directory (sys_id, path, directory, authority, tier, session, create_time) values
         | (1, '${dire.getPath}', '${dire.getPath.getName}','${dire.getPermission.getGroupAction.toString}',
         |  ${dire.getPath.toString.split("/", -1).size - 3}, ${Engine.session} , now())
         |
       """.stripMargin


    Engine.dao.saveData(sql)
  }
}