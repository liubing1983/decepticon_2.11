package com.lb.flink19.broadcaststream

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.streaming.api.datastream.{BroadcastStream, DataStreamSource}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.table.descriptors.Kafka
import org.slf4j.LoggerFactory

/**
  * @ClassName BroadcastStreamKafkaDemo
  * @Description @TODO 1. 从kafka中读取数据  2. 从redis中读取监控规则   3.使用BroadcastStream实时变更监控规则
  * @Author liubing
  * @Date 2021/6/23 11:11
  * @Version 1.0
  **/
object BroadcastStreamKafkaDemo  extends  App{

  val log = LoggerFactory.getLogger(BroadcastStreamKafkaDemo.getClass)
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  log.debug("debug")
  log.info("info")


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


  // redis 配置
         val redisConf =  FlinkJedisPoolConfig.Builder.setHost("ip").setPort(30420).setPassword("passwd").build()
        val source: DataStreamSource = env.addSource(new RedisSource(conf,new MyRedisCommandDescription(MyRedisCommand.HGET,"flink")))
        DataStream> max = source.flatMap(new MyMapRedisRecordSplitter()).timeWindowAll(Time.milliseconds(5000)).maxBy(1);
        max.print().setParallelism(1);
        executionEnvironment.execute();


  // 广播变量
  val CONFIG_DESCRIPTOR = new MapStateDescriptor[Int, Int ]("BroadcastStreamSocketDemo", classOf[Int], classOf[Int])
  val broadcastStream : BroadcastStream[MonitorStream] = env.

    env.socketTextStream("127.0.0.1", 9001).map{ x =>
    val s = x.split(",")
    // 关键字, 监控数量
    MonitorStream.apply(s(0), s(1).toInt)
  }.setParallelism(1).broadcast(CONFIG_DESCRIPTOR)

  // 数据流
  val dataStream: DataStream[String] = env.socketTextStream("127.0.0.1", 9000)

}
