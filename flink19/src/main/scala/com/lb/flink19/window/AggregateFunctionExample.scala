package com.lb.flink19.window

import org.apache.flink.api.common.functions.{AggregateFunction, Partitioner}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.slf4j.LoggerFactory
import org.apache.flink.api.scala._

case class Ab(a: String, b: Long)
// key, 最大值, 最小值, 总数, 个数
case class AggDemoBean(s: String, max: Long, min: Long, sum: Long, num: Long)


/**
  * @ClassName AggregateFunctionExample
  * @Description @TODO
  * @Author liubing
  * @Date 2020/1/9 15:23
  * @Version 1.0
  **/
object AggregateFunctionExample extends App {

  val log = LoggerFactory.getLogger(AggregateFunctionExample.getClass)

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  // nc -l 9000

  /**
    * nc -l 9000
    * 数据格式:  key,num  [a,20]
    */
  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)


  ds
    .map(Tuple1[String](_))  // partitionCustom 必须使用Tuples, POJOs, case classes, etc格式, 否则报错: This key operation requires a composite type such as Tuples, POJOs, case classes, etc
    .partitionCustom(new CustomPartitioner, 0)  // (分区函数, key取值下标)
    .map { x =>
      println(x._1)
      val v = x._1.split(",", -1)
      Ab.apply(v(0), v(1).toLong)
    }.keyBy(_.a).timeWindow(Time.seconds(10)).aggregate(new TestAggregateFunction).print()

  env.execute("123")
}

// 自定义分区器，将不同的Key(用户ID)分到指定的分区
// key: 根据key的值来分区
// numPartitions: 下游算子并行度
class CustomPartitioner extends Partitioner[String] {
  override def partition(key: String, numPartitions: Int): Int = {
    println(numPartitions+"-------------------")
    key match {
      case "a" => 0
      case "b" => 1
      case _ => numPartitions - 1  //numPartitions为当前线程数, local默认cpu数.  分区存储在数值中, 取值下标需-1, 否则报数组越界异常
    }
  }
}


class TestAggregateFunction extends AggregateFunction[Ab, AggDemoBean, (String, Long, Long, Long, Long, Double)] {

  // 每一条输入数据，和迭代数据如何迭代
  override def add(in: Ab, acc: (AggDemoBean)): (AggDemoBean) = {
    print(in.a)
    println(in.b)
    AggDemoBean.apply(in.a, if (acc.max > in.b) acc.max else in.b, if (acc.min < in.b && acc.min > 0) acc.min else in.b, acc.sum + in.b, acc.num + 1)
  }

  // 迭代状态的初始值
  override def createAccumulator(): (AggDemoBean) = AggDemoBean.apply("", 0l, 0l, 0L, 0l)


  // 返回数据，对最终的迭代数据如何处理，并返回结果
  // 返回(key, 最大值, 最小值, 总数, 个数, 平均数)
  override def getResult(acc: (AggDemoBean)): (String, Long, Long, Long, Long, Double) = {
    (acc.s, acc.max, acc.min, acc.sum, acc.num, acc.sum / acc.num)
  }

  // 多个分区的迭代数据如何合并
  override def merge(a: (AggDemoBean), b: (AggDemoBean)): (AggDemoBean) = {
    println("----------------")
    AggDemoBean.apply(s"${a.s}|${b.s}",
      if (a.max > b.max) a.max else b.max,
      if (a.min < b.min) a.min else b.min,
      a.sum + b.sum,
      a.num + b.num
    )
  }
}

