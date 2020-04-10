package com.lb.flink19.state

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor, StateTtlConfig, ValueState}
import org.apache.flink.api.common.time.Time
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

/**
  * @ClassName MapValueStateDemo
  * @Description @TODO 状态的基础应用
  * @Author liubing
  * @Date 2020/1/16 14:48
  * @Version 1.0
  **/
object MapValueStateDemo extends App {

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  // nc -l 9000
  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  import org.apache.flink.api.scala._

  ds.map { x =>
    val v = x.split(",", -1)
    (v(0), v(1).toLong)
  }.keyBy(_._1)
    .flatMap {
      new RichFlatMapFunction[(String, Long), (String, Long, Long)] {

        private var mapState: MapState[String, Long] = null

        // 从state中取值
        override def open(parameters: Configuration): Unit = {

          // 配置state生命周期
          val stateTtlConfig = StateTtlConfig
            .newBuilder(Time.seconds(10))  // 过期时间
            .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)  // 更新策略
            .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
            .build()

          // StateTtlConfig 可配置参数
          // UpdateType、StateVisibility、Time、CleanupStrategies  TtlTimeCharacteristic

          // UpdateType(Disabled、OnCreateAndWrite、OnReadAndWrite)
          // StateVisibility(ReturnExpiredIfNotCleanedUp、NeverReturnExpired)
          // TtlTimeCharacteristic(ProcessingTime)

          val mapValueState = new MapStateDescriptor[String, Long]("", classOf[String], classOf[Long])

          // 指定生命周期配置
          mapValueState.enableTimeToLive(stateTtlConfig)

          mapState = getRuntimeContext.getMapState(mapValueState)
        }

        /**
          *
          * @param in : key, 当前值
          * @param collector : key, 当前值, 最小值
          */
        override def flatMap(in: (String, Long), collector: Collector[(String, Long, Long)]): Unit = {
          // 判断当前key在状态缓存中是否存在
          val tmpValue = if (!mapState.contains(in._1)) {
            mapState.put(in._1, in._2)
            in._2
          } else { // 判断当前是否是最小值
            if (mapState.get(in._1) < in._2) { mapState.get(in._1)
            } else { mapState.put(in._1, in._2); in._2  }
          }
          // 返回: key, 当前值, 最小值
          collector.collect(in._1, in._2, tmpValue)
        }
      }
    }.print()

  env.execute()
}