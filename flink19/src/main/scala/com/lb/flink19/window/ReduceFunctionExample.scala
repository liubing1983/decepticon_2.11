package com.lb.flink19.window

import com.lb.flink19.demo.WordCount.env
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * @ClassName ReduceFunctionExample
  * @Description @TODO
  * @Author liubing
  * @Date 2020/1/8 17:21
  * @Version 1.0
  **/
object ReduceFunctionExample extends App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  import org.apache.flink.api.scala._

  ds.map{ x =>
    val v = x.split(",", -1)
    (v(0),   v(1).toDouble)
  }.keyBy(0)
    .timeWindow(Time.seconds(10))
    .reduce{
    (a, b) => (a._1, a._2 + b._2)
  }.print

  env.execute("flink ReduceFunction  Example")
}
