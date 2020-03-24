package com.lb.flink19.broadcaststream

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.BroadcastStream
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * @ClassName BroadcastStreamSocketDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/19 16:07
  * @Version 1.0
  **/

case class MonitorStream(k: String, v: Int)

object BroadcastStreamSocketDemo  extends  App{

  val log = LoggerFactory.getLogger(BroadcastStreamSocketDemo.getClass)
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  log.debug("debug")
  log.info("info")


  // 广播变量
  val CONFIG_DESCRIPTOR = new MapStateDescriptor[Int, Int ]("BroadcastStreamSocketDemo", classOf[Int], classOf[Int])
  val broadcastStream : BroadcastStream[MonitorStream] = env.socketTextStream("127.0.0.1", 9001).map{ x =>
    val s = x.split(",")
    // 关键字, 监控数量
    MonitorStream.apply(s(0), s(1).toInt)
  }.setParallelism(1).broadcast(CONFIG_DESCRIPTOR)

  // 数据流
  val dataStream: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

 //  dataStream.flatMap(_.split(" ")).map((_, 1)).keyBy(0).timeWindow(Time.seconds(5), Time.seconds(1)).sum(1).print



  //广播流合主流的整合
  dataStream
    .flatMap(_.split(" ")).map((_, 1)).keyBy(0)  // 计算关键字出现的次数
    .timeWindow(Time.seconds(5), Time.seconds(1)).sum(1)
    .connect(broadcastStream).process(new BroadcastProcessFunction[(String, Int), MonitorStream, String] {

    private var map = new mutable.HashMap[String, Int ]

    override def open(parameters: Configuration): Unit = {

      super.open(parameters)
    }

    override def processElement(value: (String, Int), ctx: BroadcastProcessFunction[(String, Int), MonitorStream, String]#ReadOnlyContext, out: Collector[String]): Unit = {

      log.debug(s"111111111: ${value._1}-${value._2}, 当前规则数量: ${map.size}.")

      // 判断当前数据是否需要监控
      map.get(value._1) match {
        case None => None
        case _ => if(value._2 > map.get(value._1).get) println(s" ${value._1} 大于监控值. 当前值${value._2}, 阈值${map.get(value._1).get} ")
      }

    }

    // 通过广播变量处理新增的监控规则
    override def processBroadcastElement(value: MonitorStream, ctx: BroadcastProcessFunction[(String, Int), MonitorStream, String]#Context, out: Collector[String]): Unit = {
      map.put(value.k, value.v)
      log.info(s"新增监控规则: ${value.k}-${value.v}, 当前规则数量: ${map.size}.")
      log.info(s"当前规则数量: ${map.mkString(" ;")}.")
    }
  }).print

  env.execute("Flink BroadcastStreamSocketDemo ")


}
