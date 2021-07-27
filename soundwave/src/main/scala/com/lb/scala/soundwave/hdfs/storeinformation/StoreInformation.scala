package com.lb.scala.soundwave.hdfs.storeinformation

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import com.lb.scala.soundwave.ActorApp
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

/**
  * @ClassName StoreInformation
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/21 15:53
  * @Version 1.0
  **/
class StoreInformation extends Actor with ActorLogging {

  var fs : FileSystem = _

  override def preStart(): Unit = {
    super.preStart()
    val conf = new Configuration();

    // 第一种：普通分布式集群链接信息
    conf.set("fs.defaultFS", "hdfs://cdh1:8020")

    // 第二种方式，链接高可用集群，把所有的配置文件都拿过来，加入到代码里去
    //    conf.addResource("conf/core-site.xml")
    //    conf.addResource("conf/hdfs-site.xml")
    //    conf.addResource("conf/mapred-site.xml")
    //    conf.addResource("conf/yarn-site.xml")

    // 第三种，连接高可用集群，使用set方式加入参数
    //		conf.set("fs.defaultFS", "hdfs://myha01");
    //		conf.set("dfs.nameservices", "myha01");
    //		conf.set("dfs.ha.namenodes.myha01", "nn1,nn2");
    //		conf.set("dfs.namenode.rpc-address.myha01.nn1", "hadoop02:9000");
    //		conf.set("dfs.namenode.rpc-address.myha01.nn2", "hadoop03:9000");

    // 配置hdfs用户, 否则会有权限问题
    System.setProperty("HADOOP_USER_NAME", "hdfs")
    fs = FileSystem.get(conf)
  }


  override def receive: Receive = {
    case path: String  => fs.listStatus(new Path(path)).map{ x =>
      log.info(x.getPath.toUri.getPath)
      //fs.isFile(x.getPath) match

    }
  }
}

object StoreInformation extends ActorApp("StoreInformation") {

  val ref = system.actorOf(Props[StoreInformation], "StoreInformationActorRef")

  ref ! "/"

}
