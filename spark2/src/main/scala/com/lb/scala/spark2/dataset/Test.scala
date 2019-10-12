package com.lb.scala.spark2.dataset

object Test  extends App{

  val l1 = List(1,2,3,4,5,6,7,8,9)
  val l2 = List(1,2,3,4,5,6,7,8,9)

  val a = for(i <- 0 until l1.size) yield {
    (l1(i) , l2(i))
  }

  println(a)
  a.foreach(println)

}
