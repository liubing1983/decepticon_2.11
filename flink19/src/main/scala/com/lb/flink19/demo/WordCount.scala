package com.lb.flink19.demo

import org.apache.calcite.sql.advise.SqlSimpleParser.Tokenizer
import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, ProcessingTimeSessionWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.dataformat.DataFormatConverters.TimeConverter

/**
  * @ClassName WordCount
  * @Description @TODO
  * @Author liubing
  * @Date 2020/1/7 14:11
  * @Version 1.0
  **/
object WordCount extends App{

  //  nc -l 9000

  // 初始化上下文
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // 指定时间类型
  // env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

  // 绑定数据源
  val t: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  import org.apache.flink.api.scala._

  // 处理数据


  val data = t.flatMap(k => k.split(" ")).map(v => (v, 1)).keyBy(0)

  // 处理数据
// data.sum(1).print

  // 使用窗口处理数据
 // data .timeWindow(Time.seconds(2),Time.seconds(1)) //指定计算数据的窗口大小和滑动窗口大小
  //  .sum(1).print

  // 会话窗口
  data.window(EventTimeSessionWindows.withGap(Time.seconds(5))).sum(1).print
  data.window(ProcessingTimeSessionWindows.withGap(Time.seconds(5))).sum(1).print



  // 执行程序
  env.execute("flink WordCount demo")
}
