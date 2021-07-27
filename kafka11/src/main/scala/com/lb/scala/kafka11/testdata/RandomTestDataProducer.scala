package com.lb.scala.kafka11.testdata

import java.time.{LocalDateTime, LocalTime}
import java.util.Properties

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import com.lb.scala.soundwave.tools.RandomData
import org.apache.kafka.clients.producer._
import org.slf4j.LoggerFactory

import scala.util.Random

/**
  * @ClassName RandomTestDataProducer
  * @Description @TODO 生产测试数据
  * @Author liubing
  * @Date 2020/3/26 15:41
  * @Version 1.0
  **/


// 模拟发送数据
case class RandomTestDataBean(brand: String, create_time: Long, price: Double, desc: String, day: String, hour: String)

object RandomTestDataProducer extends App {


  val log = LoggerFactory.getLogger(RandomTestDataProducer.getClass)


  val props = new Properties()

  // 必须指定
  props.put("bootstrap.servers", "localhost:9092 ")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // 可选配置
  props.put("acks", "1")
  // props.put("retries", 3)
  // props.put("batch.size", 323840)
  // props.put("linger.ms", 10)
  // props.put("buffer.memory", 33554432)
  // props.put("max.block.ms", 3000)

  val producer: Producer[String, String] = new KafkaProducer[String, String](props)
  val arr: Array[String] = Array[String]("apple", "华为", "锤子", "oppo", "三星", "魅族", "摩托罗拉", "诺基亚", "HTC", "小米")


  for (i <- 0 until  1000000000) {

    val localDateTime =  LocalDateTime.now()
    val bean = new RandomTestDataBean(
      arr(Random.nextInt(10)),
      System.currentTimeMillis(),
      Random.nextDouble() * Random.nextInt(10000),
      RandomData.getRandomChineseCharacters(Random.nextInt(50)),
      s"""${localDateTime.getYear}-${localDateTime.getMonthValue}-${LocalDateTime.now().getDayOfMonth}""",
      LocalDateTime.now().getHour.toString
    )
    // 使用Fastjson, 将case class转换json 因可变参数error, 需添加SerializeConfig.
    val conf = new SerializeConfig(true)
    val json_data = JSON.toJSONString(bean, conf)

    // println(json_data)
    // producer.send(new ProducerRecord[String, String]("flink-RandomTestData-topic", Integer.toString(i + 1), json_data.toString))
    producer.send(new ProducerRecord[String, String]("test2", Integer.toString(i + 1), json_data.toString))
    Thread.sleep(5)
  }
  producer.close()
}
