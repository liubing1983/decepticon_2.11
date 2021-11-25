package com.lb.flink19.demo

import com.lb.flink19.demo.source.MysqlSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import org.apache.flink.streaming.api.scala._

case class MysqlSourceBean(id: String, guid: String, name: String, update_time: String)


/**
  * @ClassName SourceDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/3 11:14
  * @Version 1.0
  **/
object SourceDemo extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val stream = env.addSource(new MysqlSource())

  stream.print()

  env.execute("flink source demo")

  // FileProcessingMode.PROCESS_CONTINUOUSLY

}
