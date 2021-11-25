package com.lb.scala.flink114.dataset

import org.apache.flink.api.scala._
import org.slf4j.LoggerFactory

/**
  * @ClassName HdfsFileDate
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/11 10:05
  * @Version 1.0
  **/
object HdfsFileDate extends App {

  val log = LoggerFactory.getLogger(HdfsFileDate.getClass)

  log.debug("1111111111111111111111111133333333333")
  log.info("11111111111111111111111111")

  // 创建一个执行环境，表示当前执行程序的上下文。

  // 返回本地执行环境，需要在调用时指定默认的并行度。
  // val env = ExecutionEnvironment.createLocalEnvironment(1)

  // 如果程序是独立调用的，则此方法返回本地执行环境；如果从命令行客户端调用程序以提交到集群，则此方法返回此集群的执行环境，
  // 也就是说，getExecutionEnvironment会根据查询运行的方式决定返回什么样的运行环境
  // 最常用的一种创建执行环境的方式。
   val env  = ExecutionEnvironment.getExecutionEnvironment

  // 返回集群执行环境，将Jar提交到远程服务器。需要在调用时指定JobManager的IP和端口号，并指定要在集群中运行的Jar包。
   // val env = ExecutionEnvironment.createRemoteEnvironment("cdh1", 8081, "/Users/liubing/IdeaProjects/decepticon_2.11/flink114/target/flink114-1.0-SNAPSHOT.jar")


  // env.setRuntimeMode(RuntimeExecutionMode.BATCH)


  val data = env.readTextFile("hdfs://cdh1:8020/user/hive/warehouse/a.txt")

  val reduceData = data.flatMap(_.split(":")).map((_, 1)).groupBy(0).reduce((x, y) => (x._1, x._2 + y._2))

  val sumData = data.flatMap(_.split(":")).map((_, 1)).groupBy(0).sum(1)


  reduceData.print()
  sumData.print()

  // env.execute("HdfsFileDate")

  //val  jobClient : JobClient = env.executeAsync()

 // println(s"getJobID: ${jobClient.getJobID.toString}")

}
