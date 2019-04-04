package com.lb.scala.kafka.demo

object ProducerDemo {

  // 创建topic
  // kafka-topics.sh --create --zookeeper localhost:2181 --topic test -- partitions 1 --replication-factor 1
  // 查看topic信息
  // kafka-topics.sh --describe --zookeeper localhost:2181 --topic test
  // 发送消息
  // kafka-console-producer.sh --broker-list localhost:9092 --topic test
  // 消费消息
  // kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

}
