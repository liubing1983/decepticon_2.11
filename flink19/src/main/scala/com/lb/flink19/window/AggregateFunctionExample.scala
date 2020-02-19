package com.lb.flink19.window

import org.apache.flink.api.common.functions.{AggregateFunction, Partitioner}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

case  class Ab(a: String, b: Long)

/**
  * @ClassName AggregateFunctionExample
  * @Description @TODO
  * @Author liubing
  * @Date 2020/1/9 15:23
  * @Version 1.0
  **/
object AggregateFunctionExample  extends  App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  // nc -l 9000
  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  import org.apache.flink.api.scala._

  ds.partitionCustom(new CustomPartitioner, ("_"))
    .map { x =>
    val v = x.split(",", -1)
    (v(0), v(1).toLong)
  }.keyBy(_._1).timeWindow(Time.seconds(10)).aggregate(new TestAggregateFunction).print()

   env.execute()
}

// 自定义分区器，将不同的Key(用户ID)分到指定的分区
// key: 根据key的值来分区
// numPartitions: 下游算子并行度
  class CustomPartitioner extends Partitioner[String]{
    override def partition(k: String, i: Int): Int = {
        k match {
          case "a" => 0
          case "b" => 0
          case _ => i
        }
    }
  }

// key, 最大值, 最小值, 总数, 个数
case class AggDemoBean(s: String, max: Long, min: Long, sum: Long, num: Long)

class TestAggregateFunction extends  AggregateFunction[(String, Long), (AggDemoBean), (String, Long, Long, Long, Long, Double) ]{

  // 每一条输入数据，和迭代数据如何迭代
  override def add(in: (String, Long), acc: (AggDemoBean)): (AggDemoBean) = {
    print(in._1) ; println(in._2)
    AggDemoBean.apply(in._1, if(acc.max > in._2) acc.max else in._2 , if(acc.min < in._2 &&  acc.min > 0) acc.min else in._2 , acc.sum + in._2, acc.num + 1)
  }

  // 迭代状态的初始值
  override def createAccumulator(): (AggDemoBean) = AggDemoBean.apply("", 0l , 0l, 0L,  0l)


  // 返回数据，对最终的迭代数据如何处理，并返回结果
  // 返回(key, 最大值, 最小值, 总数, 个数, 平均数)
  override def getResult(acc: (AggDemoBean)): (String, Long, Long, Long, Long, Double) = {
    ( acc.s, acc.max, acc.min, acc.sum, acc.num,  acc.sum / acc.num)
  }

  // 多个分区的迭代数据如何合并
  override def merge(a: (AggDemoBean), b: (AggDemoBean)): (AggDemoBean) = {
    println("----------------")
    AggDemoBean.apply(s"${a.s}|${b.s}",
      if(a.max > b.max) a.max else b.max,
      if(a.min < b.min) a.min else b.min,
      a.sum + b.sum,
      a.num + b.num
    )
  }
}

