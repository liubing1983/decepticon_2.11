package com.lb.flink19.state

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{MapStateDescriptor, StateTtlConfig, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.time.Time
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

/**
  * @ClassName StateDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/1/14 15:31
  * @Version 1.0
  **/
object StateDemo  extends  App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  // nc -l 9000
  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  import org.apache.flink.api.scala._

  ds .map { x => (x.split(",", -1)(0), x.split(",", -1)(1).toLong) }.keyBy(_._1) .flatMap{
    new RichFlatMapFunction[(String, Long), (String, Long, Long)] {

      private var s : ValueState[Long] = null

      override def open(parameters: Configuration): Unit = {

        // 配置状态生命周期
        val stateTtlConfig = StateTtlConfig
          .newBuilder(Time.seconds(10))
          .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
          .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
          .build()

        val valueState = new ValueStateDescriptor[Long]("abc", classOf[Long])

        // 指定生命周期配置
        // valueState.enableTimeToLive(stateTtlConfig)

        s = getRuntimeContext.getState(valueState)

      }

      override def flatMap(in: (String, Long), collector: Collector[(String, Long, Long)]): Unit = {
        val leastV = s.value()

        if(leastV == null) println("123")

        println(leastV+"==============")
        if(in._2 > leastV || null == leastV)
          collector.collect(in._1, in._2, leastV)
        else {
          s.update(in._2)
          collector.collect(in._1, in._2, in._2)
        }

      }
    }
  }.print()


env.execute()
}
