package com.lb.scala.flink.sqlapi

import java.util.Properties

import com.lb.scala.flink.KafkaStringSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka._
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.table.sinks.CsvTableSink
import org.apache.flink.table.api.scala._

object Kafka2Es {

  case class A(a: String , b: String )

  def main(args: Array[String]): Unit = {


    // ***************
    // STREAMING QUERY
    // ***************
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.table.api.TableConfig
    val tc: TableConfig  = new TableConfig


    // create a TableEnvironment for streaming queries
    val tableEnv = TableEnvironment.getTableEnvironment(env, tc)

    val  properties = new Properties()
    properties.put("bootstrap.servers", "127.0.0.1:9092")
    properties.put("zookeeper.connect", "127.0.0.1:2181")
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("group.id", "test6")


    val  myConsumer= new FlinkKafkaConsumer010[String]("lb", KafkaStringSchema, properties)

    val  stream: DataStream[String] = env.addSource(myConsumer)

    val m : DataStream[(String, String)] = stream.map{ x =>
     val a =  x.split(",")
      (a(0), a(1))
    }

    var tableA = tableEnv.fromDataStream(m, 'a, 'b)

    //注册为user表
    tableEnv.registerDataStream("Users", m, 'a , 'b)

    val csvSink = new CsvTableSink("/Users/liubing/Desktop/2")
    val fieldNames: Array[String] = Array("product", "amount")
    val fieldTypes: Array[TypeInformation[_]] = Array(Types.STRING, Types.STRING)
    tableEnv.registerTableSink("RubberOrders", fieldNames, fieldTypes, csvSink)
    // run a SQL update query on the Table and emit the result to the TableSink
    tableEnv.sqlUpdate("INSERT INTO RubberOrders SELECT b, a FROM Users ")

    env.setParallelism(1)
    // 执行
    env.execute()

  }

}
