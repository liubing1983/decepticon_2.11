package com.lb.scala.base.functional.p07.example

object e7_1 {

  def sum(ints: Seq[Int]): Int = {
    ints.foldLeft(0)(_ + _)
  }

  def sum(ints: IndexedSeq[Int]): Int = {
    if (ints.length <= 1) {
      ints.headOption getOrElse 0
    }
    else {
      // 使用splitAt将序列平分
      val (l, r) = ints.splitAt(ints.length / 2)
      // 对两部分分别递归求和, 再将结果累加
      sum(l) + sum(r)

    }
  }
}
