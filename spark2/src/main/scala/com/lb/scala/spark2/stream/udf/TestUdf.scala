package com.lb.scala.spark2.stream.udf

object TestUdf {

  def testAbc(s: String): String = {
    s.length+""
  }
}
