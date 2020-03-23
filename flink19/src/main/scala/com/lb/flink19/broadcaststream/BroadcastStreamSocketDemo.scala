package com.lb.flink19.broadcaststream

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

/**
  * @ClassName BroadcastStreamSocketDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/19 16:07
  * @Version 1.0
  **/
object BroadcastStreamSocketDemo  extends  App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment


  // 广播变量
  val CONFIG_DESCRIPTOR = new MapStateDescriptor[String, String]("streamingConfig", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)
  val broadcastStream = env.socketTextStream("127.0.0.1", 9001).setParallelism(1).broadcast(CONFIG_DESCRIPTOR)

  // 数据流
  val dataStream: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)



  //广播流合主流的整合
  dataStream.connect(broadcastStream).process(new BroadcastProcessFunction[String, String, String] {
    private var s = ""

    override def open(parameters: Configuration): Unit = {

      super.open(parameters)
    }

    override def processElement(value: String, ctx: BroadcastProcessFunction[String, String, String]#ReadOnlyContext, out: Collector[String]): Unit = {

      println("当前监控关键字：" + s)
      if(value.contains(s)){
        println(s"监控到关键字：${value}")
      }
    }

    override def processBroadcastElement(value: String, ctx: BroadcastProcessFunction[String, String, String]#Context, out: Collector[String]): Unit = {
      println(s"广播值: ${value}")
      s = value
    }
  })

  env.execute("FlinkBroadcaseJoinDemo")


}
