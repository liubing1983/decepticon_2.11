package com.lb.scala.flink114.dataset

import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.slf4j.LoggerFactory

/**
  * @ClassName WordCount
  * @Description @TODO 基于StreamExecutionEnvironment实现batch
  * @Author liubing
  * @Date 2021/11/15 15:44
  * @Version 1.0
  **/
object WordCount {


  val log = LoggerFactory.getLogger(WordCount.getClass)


  def main(args: Array[String]): Unit = {
    // 创建一个执行环境，表示当前执行程序的上下文。


    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 返回集群执行环境，将Jar提交到远程服务器。需要在调用时指定JobManager的IP和端口号，并指定要在集群中运行的Jar包。
    // val env = StreamExecutionEnvironment.createRemoteEnvironment("cdh1", 8081, "/Users/liubing/IdeaProjects/decepticon_2.11/flink114/target/flink114-1.0-SNAPSHOT.jar")


    env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC)


    // val data = env.readTextFile("hdfs://cdh1:8020/user/hive/warehouse/a.txt")
    val data: DataStream[String] = env.socketTextStream("localhost", 9999)

    data.print()


    env.execute("123")

    //val  jobClient : JobClient = env.executeAsync()

    //println(s"getJobID: ${jobClient.getJobID.toString}")

  }


}
