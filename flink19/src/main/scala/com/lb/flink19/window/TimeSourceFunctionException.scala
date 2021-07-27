package com.lb.flink19.window

import com.lb.scala.soundwave.LbApp
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.{SocketTextStreamFunction, SourceFunction}
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * @ClassName TimeSourceFunctionException
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/10 15:27
  * @Version 1.0
  **/
object TimeSourceFunctionExample  extends  LbApp("TimeSourceFunctionExample"){

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // 设置time为EventTime
   env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)


  val ds =  env.socketTextStream("127.0.0.1", 9000)


  import org.apache.flink.api.scala._


  ds.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[String](Time.milliseconds(3000)) {
    override def extractTimestamp(time: String): Long = {
      // / EventTime是日志生成时间,从日志中解析EventTime
      // val eventTime = time.split(" ")(0).toLong
      println("eventTime = " + time)
      //eventTime
      System.currentTimeMillis()
    }
  }).map{ x => (x.split(",", -1)(0),  x.split(",", -1)(1).toInt, System.currentTimeMillis()) }
    //.assignAscendingTimestamps{_._3}
    .keyBy(0)
    .timeWindow(Time.seconds(5))
    .sum("_2")
    .print()

  env.execute("flink ReduceFunction  Example")

}


