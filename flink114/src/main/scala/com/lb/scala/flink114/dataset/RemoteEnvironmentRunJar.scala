package com.lb.scala.flink114.dataset

import org.apache.flink.api.scala.ExecutionEnvironment

/**
  * @ClassName RemoteEnvironmentRunJar
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/15 10:47
  * @Version 1.0
  **/
object RemoteEnvironmentRunJar extends  App{

  val env = ExecutionEnvironment.createRemoteEnvironment("cdh1", 8081, "/Users/liubing/IdeaProjects/decepticon_2.11/flink114/target/flink114-1.0-SNAPSHOT.jar")


}
