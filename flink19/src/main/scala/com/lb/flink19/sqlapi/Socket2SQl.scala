package com.lb.flink19.sqlapi

import com.lb.scala.soundwave.LbApp
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.scala.StreamTableEnvironment

/**
  * @ClassName Socket2SQl
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/15 11:34
  * @Version 1.0
  **/
object Socket2SQl extends LbApp("Socket2SQl"){

  // 初始化上下文
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  val tableEnv = StreamTableEnvironment.create(env)

  val ds = env.socketTextStream("127.0.0.1", 9000)

  tableEnv.registerDataStream("table1", ds)

  tableEnv.sqlQuery("select * from table1 ")

}
