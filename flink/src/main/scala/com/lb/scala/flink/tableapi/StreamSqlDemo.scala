package com.lb.scala.flink.tableapi

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{Table, TableEnvironment, Types}
import org.apache.flink.table.sinks._

object StreamSqlDemo {

  def main(args: Array[String]): Unit = {

    // ***************
    // STREAMING QUERY
    // ***************
     val sEnv = StreamExecutionEnvironment.getExecutionEnvironment
    // create a TableEnvironment for streaming queries
     val sTableEnv = TableEnvironment.getTableEnvironment(sEnv)

    // get input data by connecting to the socket
    val ds: DataStream[String] = sEnv.socketTextStream("localhost", 4321, '\n')


    sTableEnv.registerDataStream("myTable", ds)


    val sink  = new CsvTableSink("/Users/liubing/Desktop")

    val fieldNames: Array[String] = Array("a")
    val fieldTypes: Array[TypeInformation[_]] = Array(Types.STRING)
    sTableEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, sink)

    println("-------")
    sTableEnv.sqlUpdate("insert into CsvSinkTable  select abbb from myTable")
    //revenue.insertInto("CsvSinkTable")

    sEnv.setParallelism(1)
    // 执行
    sEnv.execute()

  }

}
