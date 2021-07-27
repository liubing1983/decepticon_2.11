package com.lb.scala.kafka11.demo

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.lb.scala.kafka11.testdata.RandomTestDataProducer
import org.apache.kafka.clients.producer._
import org.slf4j.LoggerFactory

/**
  * @ClassName ProducerDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/26 16:10
  * @Version 1.0
  **/
object ProducerDemo  extends App{

  val log =  LoggerFactory.getLogger(RandomTestDataProducer.getClass)


    val props = new Properties()

    // 必须指定
    props.put ("bootstrap.servers", "localhost:9092 ")
    props.put ("key.serializer",   "org.apache.kafka.common.serialization.StringSerializer")
    props.put ("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    // 可选配置
//    props.put ("acks", "1")
//    props.put ("retries", 3)
//    props.put ("batch.size", 323840)
//    props.put ("linger.ms", 10)
//    props.put ("buffer.memory", 33554432)
//    props.put ("max.block.ms", 3000)

    val producer: Producer[String, String] = new KafkaProducer[String, String](props)

    for (i <- 0 to 100){
      // 异步发送
      // 不获取发送结果
      producer.send(new ProducerRecord[String, String]("my-topic1", Integer.toString(i+1), Integer.toString(i)))

      // 使用Callback 获取发送结果,  Callback可以自定义
      producer.send(new ProducerRecord[String, String]("my-topic2", Integer.toString(i+2), Integer.toString(i)), new Callback(){
        // RecordMetadata 和 Exception 在成功或失败的时候会有一个为null
        @Override
        def onCompletion( metadata: RecordMetadata,  ex: Exception) ={
          if(ex == null){  // 消息发送成功
            log.info(metadata.topic())

          }else{  // 消息发送失败
            log.error(ex.getMessage())
            ex.printStackTrace()
          }
        }
      })

      // 同步发送
      // 不限制发送时间
      producer.send(new ProducerRecord[String, String]("my-topic3", Integer.toString(i+3), Integer.toString(i))).get()

      // 限制发送时间
      producer.send(new ProducerRecord[String, String]("my-topic4", Integer.toString(i+4), Integer.toString(i))).get(5, TimeUnit.SECONDS)
    }

    producer.close()



}
