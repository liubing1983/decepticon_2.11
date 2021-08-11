package com.lb.scala.devastator.hivepartition

import java.util.concurrent.atomic.LongAdder

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.finupcredit.base.basetools.{ConfigFiles, DBConnectionInfo, MysqlDao}
import com.lb.scala.devastator.Engine
import com.lb.scala.devastator.hivepartition.HivePartitionCheck.refNum
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.JavaConversions._

/**
  * @ClassName HivePartitionCheck
  * @Description @TODO
  * @Author liubing
  * @Date 2021/7/30 10:35
  * @Version 1.0
  **/
class HivePartitionCheck extends Actor with ActorLogging {

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
    refNum.decrement()
    super.postStop()
  }

  override def receive: Receive = {
    case (part: HivePartitionBean, dao: MysqlDao) => abc(part, dao)
  }


  /**
    * 检查hive 分区
    *
    * @param part
    */
  private def abc(part: HivePartitionBean, dao: MysqlDao): Unit = {


    val part_file_num = if (fileSystem.exists(new Path(part.PARTITION_LOCATION_URI))) {
      fileSystem.listStatus(new Path(part.PARTITION_LOCATION_URI)).size
    } else {
      null
    }

    val alter_sql = if (null == part_file_num || part_file_num == 0) {
      s"ALTER TABLE ${part.DB_NAME}.${part.TBL_NAME} DROP IF EXISTS PARTITION (${part.PART_NAME});"
    }


    val sql =
      s"""  insert into   partition_file_num_check
         |  (db_id, tab_id,part_id, part_file_num, part_session, alter_sql, create_time) values
         |  (${part.DB_ID},${part.TBL_ID},${part.PART_ID},${part_file_num}, '${part.session}', '${alter_sql}',now())
         |
       """.stripMargin
    dao.saveData(sql)

    refNum.decrement()

  }


}

object HivePartitionCheck extends App {


  val log = LogFactory.getLog(Engine.getClass)

  // 任务计数器
  var refNum: LongAdder = new LongAdder
  var refNum_sum: LongAdder = new LongAdder
  var a = 0
  // 检测批次
  val session: String = System.currentTimeMillis().toString


  // 加载数据库配置
  //val db_pro = new ConfigFiles("jdbc.properties").getProperties().get
  //val dao = new MysqlDao(new DBConnectionInfo(db_pro))


  // 加载数据库配置
  val hive_pro = new ConfigFiles("hive.properties").getProperties().get
  val hive_dao = new MysqlDao(new DBConnectionInfo(hive_pro))

  val as = ActorSystem.create("Hive-Partition-Check-ActorSystem")
  val ref = as.actorOf(Props[HivePartitionCheck].withRouter(new RoundRobinPool(2)), "Hive-Partition-Check-ActorOf")


  // 从hive原数据库中读出所有的分区信息
  log.info(hive_pro.getProperty("hivePartitionSql"))
  hive_dao.getDataList(hive_pro.getProperty("hivePartitionSql"), hive_pro.getProperty("hivePartitionColumn"), false) match {
    case None => log.error("从数据库获取hive原数据出错!!!")
    case Some(list) if (list.size() == 0) => log.warn("数据库中没有hive partitions信息!!")
    case Some(list) => list.foreach { x =>
      a = list.size()
      // DB_ID,DB_LOCATION_URI,NAME,TBL_TYPE,TBL_NAME,PART_NAME

      val PARTITION_LOCATION_URI = s"${x.getOrElse("DB_LOCATION_URI", "")}/${x.getOrElse("TBL_NAME", "")}/${x.getOrElse("PART_NAME", "")}"

      log.debug(s"db: ${x.getOrElse("DB_ID", "")}-${x.getOrElse("", "NAME")}, " +
        s"table: ${x.getOrElse("TBL_NAME", "")}, " +
        s"partitions: ${x.getOrElse("PART_NAME", "")}, " +
        s"PARTITION_LOCATION_URI: ${PARTITION_LOCATION_URI}")

      ref ! (HivePartitionBean(x.getOrElse("DB_ID", ""), x.getOrElse("DB_NAME", ""), x.getOrElse("DB_LOCATION_URI", ""), x.getOrElse("NAME", ""),
        x.getOrElse("TBL_TYPE", ""), x.getOrElse("TBL_NAME", "")
        , x.getOrElse("PART_NAME", ""), PARTITION_LOCATION_URI, x.getOrElse("TBL_ID", ""), x.getOrElse("PART_ID", ""),
        session),
        hive_dao
      )

      refNum.increment()
      refNum_sum.increment()
      if(refNum.intValue() >= 200)Thread.sleep(1000)
    }
  }


  while (true) {
    Thread.sleep(1000 * 60)
    if (refNum.intValue <= 0) {
      log.info(s"任务完成, 关闭系统!!!!!")
      as.terminate()
      System.exit(0)
    }else{
      log.info(s"总分区数: ${a},  已执行分区数: ${refNum.intValue}, 正在执行的任务数量: ${refNum.intValue}")
    }
  }

}


case class HivePartitionBean(DB_ID: String, DB_NAME: String, DB_LOCATION_URI: String, NAME: String, TBL_TYPE: String,
                             TBL_NAME: String, PART_NAME: String, PARTITION_LOCATION_URI: String,
                             TBL_ID: String, PART_ID: String, session: String)