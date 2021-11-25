package com.lb.scala.flink114

import java.util.Date

/**
  * @ClassName Test
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/12 13:05
  * @Version 1.0
  **/
object Test extends App {


  val tm1 = (new Date()).getTime
  Thread.sleep(5000)
  val tm2 = (new Date()).getTime

  println( (tm2 - tm1) / 1000 )


  private  def abc(s1: String, s2: String): Int ={

    var s = s1
    var a = "aa,bb,cc,aa"
    var b = true
    var i = 0
    while (b) {

      if (a.indexOf(s2) >= 0) {
        a = a.replaceFirst(s2, "")
        i = i + 1;
        println(s"${a}---${i}---${a.indexOf(s2)}--${a.replaceFirst(s2, "")}")
      }
      else b = false
    }
    i
  }




  println(abc("aa,bb,cc,aa", "aa"))

}
