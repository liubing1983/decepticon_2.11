package com.lb.flink19.sqlapi

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.scala.BatchTableEnvironment



/**
  * @ClassName Mysql2Sql
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/9 15:19
  * @Version 1.0
  **/
object Mysql2Sql extends App {

  val batchEnv = ExecutionEnvironment.getExecutionEnvironment
  val tableEnv = BatchTableEnvironment.create(batchEnv)

  tableEnv.scan()


}
