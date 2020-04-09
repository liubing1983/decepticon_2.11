package com.lb.flink19.window

import com.lb.scala.soundwave.LbApp
import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

case class In(name: String, num: Int)

case class Out(name: String, sum: Long, count: Int, avg: Double)

/**
  * @ClassName ProcessWindowsFunctionExample
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/9 11:44
  * @Version 1.0
  **/
object ProcessWindowsFunctionExample extends LbApp("ProcessWindowsFunctionExample") {

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  import org.apache.flink.api.scala._

  /**
    * nc -l 9000
    * 数据格式:  key,num  [a,20]
    */
  val ds: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

  ds.map { x => In.apply(x.split(",", -1)(0), x.split(",", -1)(1).toInt) }
    .keyBy(0)
    .timeWindow(Time.seconds(5))
    .process(new MyProcessWindowFunction)
    .print()

  env.execute("ProcessWindowsFunctionExample")
}

/**
  * IN, OUT, KEY, W
  */
class MyProcessWindowFunction extends ProcessWindowFunction[In, Out, Tuple, TimeWindow] {

  val mapValueState = new MapStateDescriptor[String, Out]("", classOf[String], classOf[Out])

  override def open(parameters: Configuration): Unit = {
    println("open")
    super.open(parameters)
  }

  override def process(key: Tuple, context: Context, elements: Iterable[In], out: Collector[Out]): Unit = {
    var sum: Long = 0
    var count: Int = 0
    var name: String = ""
    for (x <- elements) {
      sum = sum + x.num
      count = count + 1
      name = x.name
    }

    println(key.toString)  // 输出 (a)

    // 输入10个  a,10

    /**
      * windowState 输出
      * 6 > Out(a,60,6,10.0)
      * 6 > Out(a,30,3,10.0)
      * 6 > Out(a,10,1,10.0)
      */
    val mapState = context.windowState.getMapState(mapValueState)

    /**
      * globalState 输出
      * 6 > Out(a,60,6,10.0)
      * 6 > Out(a,100,10,10.0)
      */
    //val mapState = context.globalState.getMapState(mapValueState)

    val outCC = mapState.get(name) match {
      case outCCState: Out => {
        // windowState 不会进入该case
        println("----" + outCCState.sum)
        Out.apply(name, sum + outCCState.sum, count + outCCState.count, (sum + outCCState.sum) / (count + outCCState.count))
      }
      case _ => {
        println("---======" )
        Out.apply(name, sum, count, sum / count)
      }
    }

    mapState.put(name, outCC)

    out.collect(outCC)
  }
}
