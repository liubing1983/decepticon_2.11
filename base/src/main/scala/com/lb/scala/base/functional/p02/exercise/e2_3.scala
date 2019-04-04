package com.lb.scala.base.functional.p02.exercise

object e2_3 {

  //https://github.com/liubing1983/fpinscala/blob/master/answers/src/main/scala/fpinscala/gettingstarted/GettingStarted.scala

  /**
    * 2.3
    * @param f
    * @tparam A
    * @tparam B
    * @tparam C
    * @return
    */
  def curry[A, B, C](f: (A, B) => C): A => (B => C) = {
    a: A => b: B => f(a: A, b: B)
  }


  /**
    * 2.4
    * @param f
    * @tparam A
    * @tparam B
    * @tparam C
    * @return
    */
  def uncurry[A, B, C](f: A => B => C): (A, B) => C = {
    (a, b) => f(a)(b)
  }

  /**
    * 2.5
    * @param f
    * @param g
    * @tparam A
    * @tparam B
    * @tparam C
    * @return
    */
  def compose[A, B, C](f: B => C, g: A => B): A => C = {
    a => f(g(a))
  }

}
