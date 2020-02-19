package com.lb.flink19.sqlapi

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{ StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.{Table}
import org.apache.flink.table.api.scala.StreamTableEnvironment
import org.apache.flink.table.descriptors._
import org.apache.flink.api.common.typeinfo.{TypeInformation, Types}
import org.apache.flink.types.Row
import org.apache.flink.table.api.scala._


/**
  * @ClassName Kafka2Sql
  * @Description @TODO 从kafka中读取数据
  * @Author liubing
  * @Date 2019/12/6 14:51
  * @Version 1.0
  **/
object Kafka2Sql {

  // 初始化上下文
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  val tableEnv = StreamTableEnvironment.create(env)


  def main(args: Array[String]): Unit = {

    // kafka配置
    val properties = new Properties()
    properties.put("bootstrap.servers", "127.0.0.1:9092")
    properties.put("zookeeper.connect", "127.0.0.1:2181")
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("group.id", "test8")
    //构建FlinkKafkaConsumer
    val myConsumer = new FlinkKafkaConsumer[String]("topic", new SimpleStringSchema(), properties)
    //指定偏移量
    myConsumer.setStartFromEarliest()

    // 读取kafka数据2
    val kafka: Kafka = new Kafka().version("universal").topic("lb2").properties(properties).startFromEarliest()

    tableEnv.connect(kafka)
      .withFormat(
       new Json().failOnMissingField(true).deriveSchema()
     )
       .withSchema(
         new Schema()
           .field("user", Types.STRING)
           .field("age", Types.STRING)
       )
       .inAppendMode()
       .registerTableSource("lb")

    implicit val typeInfo = TypeInformation.of(classOf[Row])


    val t1 = tableEnv.scan("lb").groupBy('user).select('user,  'age count )
    val rowDataStream2 = tableEnv.toRetractStream(t1)
    // rowDataStream2.addSink()
    rowDataStream2.print()


   val rs: Table = tableEnv.sqlQuery("select user  from lb ")
    rs.printSchema()
    val rowDataStream = tableEnv.toRetractStream(rs)

    rowDataStream.print()

    rs.printSchema()

    tableEnv.execute("flink1.9 table api demo")
  }

}
