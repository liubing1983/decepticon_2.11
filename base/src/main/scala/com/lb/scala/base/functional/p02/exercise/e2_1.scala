package com.lb.scala.base.functional.p02.exercise

import org.slf4j.LoggerFactory

object e2_1 extends  App{

  val log = LoggerFactory.getLogger(e2_1.getClass)

  log.debug(fib(0)+" ===")


  /**
    * 斐波那契数列 - 递归调用的联系
    * @param n
    * @return
    */
  def fib(n: Int) : Int = {
    def go(n: Int, a: Int, b: Int) : Int ={
      if(n <= a) a
      else go(n, b, a+b)
    }
    go(n, 0, 1)
  }

 List(1, 2, "a")   .foreach(println)

}
