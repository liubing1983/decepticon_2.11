package com.lb.scala.soundwave.hive

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, StructType}

/**
  * @ClassName HiveUdf
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/27 10:23
  * @Version 1.0
  **/
class HiveUdf extends UserDefinedAggregateFunction{
  // 入参
  // 账期, 当期, 账单日, 应还金额
  override def inputSchema: StructType = ???

  // 缓存信息
  override def bufferSchema: StructType = ???

  // 输出结果
  override def dataType: DataType = ???


  override def deterministic: Boolean = true

  // 缓存初始化信息
  override def initialize(buffer: MutableAggregationBuffer): Unit = ???

  // 更新缓存
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = ???

  // 合并所有数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = ???

  // 计算结果
  override def evaluate(buffer: Row): Any = ???
}
