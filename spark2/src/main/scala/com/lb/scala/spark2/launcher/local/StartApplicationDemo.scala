package com.lb.scala.spark2.launcher.local

import java.util
import java.util.concurrent.CountDownLatch

import org.apache.spark.launcher.SparkLauncher
import org.apache.spark.launcher.SparkAppHandle

/**
  * @ClassName StartApplicationDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/7/9 11:12
  * @Version 1.0
  **/
object StartApplicationDemo {

  def main(args: Array[String]): Unit = {

    val countDownLatch = new CountDownLatch(1);

    val envParams = new util.HashMap[String, String]()
   // envParams.put("YARN_CONF_DIR", "/home/hadoop/cluster/hadoop-release/etc/hadoop")
   // envParams.put("HADOOP_CONF_DIR", "/home/hadoop/cluster/hadoop-release/etc/hadoop")
    envParams.put("SPARK_HOME", "/Users/liubing/software/spark-2.3.2")
    envParams.put("JAVA_HOME", "/Library/Java/JavaVirtualMachines/jdk1.8.0_171.jdk/Contents/Home");
   // envParams.put("SPARK_PRINT_LAUNCH_COMMAND", "1")

    val sparkSc = new SparkLauncher(envParams)
      .setAppResource("/Users/liubing/IdeaProjects/decepticon_2.11/spark2/target/spark2-1.0-SNAPSHOT.jar")
      .setMainClass("com.lb.scala.spark2.dataset.JdbcDfDemo")
      //.setMainClass("com.lb.scala.spark2.stream.WordCount")
      .setMaster("local[2]")
      .startApplication()

    sparkSc.addListener(


    new SparkAppHandle.Listener() { //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
      override def stateChanged(sparkAppHandle: SparkAppHandle): Unit = {
        if (sparkAppHandle.getState.isFinal) countDownLatch.countDown
        System.out.println("state:" + sparkAppHandle.getState.toString)
      }

      override def infoChanged(sparkAppHandle: SparkAppHandle): Unit = {
        System.out.println("Info:" + sparkAppHandle.getState.toString)
        println(sparkAppHandle.getAppId)
      }
    }
    )

    println(sparkSc.getAppId)
    println(sparkSc.getState)

    countDownLatch.await()
  }

}
