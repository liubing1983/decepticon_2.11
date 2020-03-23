package com.lb.flink19.broadcaststream

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.{BroadcastConnectedStream, DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

import scala.util.Random

/**
  * @ClassName BroadcastStreamDemo
  * @Description @TODO 模拟广播流实时变更监控规则
  * @Author liubing
  * @Date 2020/3/19 10:46
  * @Version 1.0
  **/
object BroadcastStreamDemo extends  App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  //创建广播流
  val CONFIG_DESCRIPTOR = new MapStateDescriptor[String, String]("streamingConfig", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)
  val broadcastStream = env.addSource(new RichParallelSourceFunction[String] {
    var isRun: Boolean = _
    override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
      val words = Array("spark", "suning")
      //每5秒变更监控规则
      for (i <- 1 to 1000) {
        val random = new Random
        val message = words(random.nextInt(2))
        ctx.collect(message)
        Thread.sleep(5000)
      }
    }
    override def cancel(): Unit = {
      isRun = false
    }
  }).setParallelism(1).broadcast(CONFIG_DESCRIPTOR)



  //创建主流
  val dataStream: DataStream[String] = env.addSource(new RichParallelSourceFunction[String] {
    var isRun: Boolean = _

    override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
      val words = Array("spark", "suning", "spring", "flink", "kafka", "hadoop")
      //这个流产生1000个单词就结束了
      for (i <- 1 to 1000) {
        val random = new Random
        val message = "模拟从kafka读取到的数据"+words(random.nextInt(6))
        ctx.collect(message)
        Thread.sleep(1000)
      }
    }

    override def cancel(): Unit = {
      isRun = false
    }
  })
    .setParallelism(1)


  //广播流合主流的整合
  val dbs: BroadcastConnectedStream[String, String] = dataStream.connect(broadcastStream)
  dbs.process(new BroadcastProcessFunction[String, String, String] {
    // private var conf = new util.HashMap[String,Int]()

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
