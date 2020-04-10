package com.lb.flink19.state

import java.util.Properties

import com.alibaba.fastjson.JSON
import com.lb.scala.soundwave.LbApp
import org.apache.flink.api.common.functions.{AggregateFunction, RichFlatMapFunction, RichMapFunction}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor, StateTtlConfig}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import org.apache.flink.api.scala._

import scala.collection.JavaConversions._


case class RandomTestDataBean(brand: String, create_time: Long, price: Double, desc: String, day: String, hour: String)

// key,  最大值, 最小, 总数, 个数, 平均数
case class AggregateBean(brand: String, day: String, hour: String, max: Double, min: Double, sum: Double, count: Long, avg: Double)


/**
  * @ClassName SimulateTrading
  * @Description @TODO  输入商品相关信息, 按小时汇总数据,每5秒计算一次
  * @Author liubing
  * @Date 2020/3/27 14:59
  * @Version 1.0
  **/
object SimulateTrading extends LbApp("SimulateTrading") {

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // kafka配置
  val properties = new Properties()
  properties.setProperty("bootstrap.servers", "localhost:9092")
  properties.setProperty("group.id", "test")
  // 从kafka读取配置, 并从最早的数据开始读取
  val json_data2: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String]("test2", new SimpleStringSchema(), properties).setStartFromEarliest())

  json_data2.map { x =>
    JSON.parseObject[RandomTestDataBean](x, classOf[RandomTestDataBean])
  }
    //.partitionCustom(new CustomPartitioner, _.brand)
    .keyBy(x => (x.brand, x.day, x.hour)) // 根据品牌,日期,小时分区
    .timeWindow(Time.seconds(5))
    .aggregate(new SimulateTradingAggregateFunction)
    .keyBy(_._1)
    .flatMap(new SimulateTradingRichMapFunction)
    .print()

  env.execute()
}


/**
  * 数据汇总, 根据key, 计算相关数据
  */
private class SimulateTradingAggregateFunction extends AggregateFunction[RandomTestDataBean, AggregateBean, (String, AggregateBean)] {

  // 每一条输入数据，和迭代数据如何迭代
  override def add(in: RandomTestDataBean, acc: AggregateBean): AggregateBean = {
    // key,  最大值, 最小, 总数, 个数, 平均数
    AggregateBean.apply(in.brand, in.day, in.hour,
      if (acc.max > in.price) acc.max else in.price, // 最大值
      if (acc.min < in.price && acc.min > 0) acc.min else in.price, // 最小值
      acc.sum + in.price,
      acc.count + 1,
      0L)
  }

  // 迭代状态的初始值
  override def createAccumulator(): AggregateBean = AggregateBean.apply("", "", "", 0, 0L, 0l, 0L, 0l)

  // 返回数据，对最终的迭代数据如何处理，并返回结果
  // 返回(key, 最大值, 最小值, 总数, 个数, 平均数)
  override def getResult(acc: AggregateBean): (String, AggregateBean) = {
    (s"""${acc.brand}${acc.day}${acc.hour}""" ,
    AggregateBean.apply(acc.brand, acc.day, acc.hour, acc.max, acc.min, acc.sum, acc.count, 0))
  }

  // 多个分区的迭代数据如何合并-只有在SessionWindow中使用
  override def merge(a: AggregateBean, b: AggregateBean): AggregateBean = {
    println("----------------")
    //    AggregateBean.apply(s"${a.key}|${b.key}",
    //      if (a.max > b.max) a.max else b.max,
    //      if (a.min < b.min) a.min else b.min,
    //      a.sum + b.sum,
    //      a.num + b.num,
    //      0
    //    )
    null
  }


}

// 实现state
// 后期使用process实现该demo的功能, 减少一次keyby和flatMap
private class SimulateTradingRichMapFunction extends RichFlatMapFunction[(String, AggregateBean), AggregateBean] {

  import org.apache.flink.api.common.time.Time

  private var mapState: MapState[String, AggregateBean] = null

  // 从state中取值
  override def open(parameters: Configuration): Unit = {
    // 配置state生命周期
    val stateTtlConfig = StateTtlConfig
      .newBuilder(Time.minutes(100)) // 过期时间
      .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite) // 更新策略
      .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
      .build()

    val mapValueState = new MapStateDescriptor[String, AggregateBean]("hahaha", classOf[String], classOf[AggregateBean])
    // 指定生命周期配置
    mapValueState.enableTimeToLive(stateTtlConfig)
    mapState = getRuntimeContext.getMapState(mapValueState)
  }

  /**
    *
    */
  override def flatMap(in: (String, AggregateBean), collector: Collector[AggregateBean]): Unit = {
    val tmp = mapState.get(in._1) match {
      case null => in._2
      case aggBean =>
        AggregateBean.apply(
          aggBean.brand, aggBean.day, aggBean.hour,
          if (aggBean.max > in._2.max) aggBean.max else in._2.max,
          if (aggBean.min < in._2.min) aggBean.min else in._2.min,
          aggBean.sum + in._2.sum,
          aggBean.count + in._2.count,
          (aggBean.sum + in._2.sum) / (aggBean.count + in._2.count)
        )
    }
    // 存在问题, 第一次计算时没有平均值.
    mapState.put(in._1, tmp)
    collector.collect(tmp)
  }
}



