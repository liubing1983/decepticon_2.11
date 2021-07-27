package com.lb.scala.flink.tableapi

import org.apache.flink.api.scala._
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.sinks.CsvTableSink

case class Abc(n: String)

object TableDemo {

  def main(args: Array[String]): Unit = {

    // ***************
    // STREAMING QUERY
    // ***************
   // val sEnv = StreamExecutionEnvironment.getExecutionEnvironment
    // create a TableEnvironment for streaming queries
   // val sTableEnv = TableEnvironment.getTableEnvironment(sEnv)

    // ***********
    // BATCH QUERY
    // ***********
    val bEnv = ExecutionEnvironment.getExecutionEnvironment
    // create a TableEnvironment for batch queries
    val bTableEnv = TableEnvironment.getTableEnvironment(bEnv)

    val input: DataSet[String] = bEnv.readTextFile("/Users/liubing/Desktop/2222222.txt")

    val t : Table = input.toTable(bTableEnv)  //bTableEnv.fromDataSet(input)

    // 注册一个 Table
    bTableEnv.registerTable("table1", t)           // 或者
    //bTableEnv.registerTableSource("table1", input)     // 或者
    //bTableEnv.registerExternalCatalog("extCat", ...)

    // 从Table API的查询中创建一个Table
//    val tapiResult = tableEnv.scan("table1").select("")
    // 从SQL查询中创建一个Table
    val sqlResult: Table  = bTableEnv.sqlQuery("SELECT * FROM table1 ")


    val ab: DataSet[Abc] =  bTableEnv.toDataSet[Abc](sqlResult)

    ab.map{x => println(x.n) }

    val sink  = new CsvTableSink("/Users/liubing/Desktop/a.csv")

    sqlResult.writeToSink(sink)

    bEnv.setParallelism(1)
    // 执行
    bEnv.execute()
  }

}
