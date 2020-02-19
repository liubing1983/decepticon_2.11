package com.lb.flink19

/**
  * @ClassName KafkaStringSchema
  * @Description @TODO
  * @Author liubing
  * @Date 2019/12/6 15:14
  * @Version 1.0
  **/


import org.apache.flink.streaming.util.serialization.{DeserializationSchema, SerializationSchema}

object KafkaStringSchema extends SerializationSchema[String] with DeserializationSchema[String] {

  import org.apache.flink.api.common.typeinfo.TypeInformation
  import org.apache.flink.api.java.typeutils.TypeExtractor

  override def serialize(t: String): Array[Byte] = t.getBytes("UTF-8")

  override def isEndOfStream(t: String): Boolean = false

  override def deserialize(bytes: Array[Byte]): String = new String(bytes, "UTF-8")

  override def getProducedType: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

}

