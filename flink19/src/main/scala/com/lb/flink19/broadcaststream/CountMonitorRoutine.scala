package com.lb.flink19.broadcaststream

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * @ClassName CountMonitorRoutine
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/23 10:52
  * @Version 1.0
  **/
object CountMonitorRoutine  extends  App{

  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val CONFIG_DESCRIPTOR = new MapStateDescriptor[String, String]("streamingConfig", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)

}
