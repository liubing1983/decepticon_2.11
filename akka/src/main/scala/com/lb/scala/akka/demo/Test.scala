package com.lb.scala.akka.demo

import java.lang.reflect.Method

import org.apache.commons.codec.binary.StringUtils

import scalaj.http._
import scala.reflect.runtime.{universe => ru}

class Abc{
 // 步骤1, 需要的算法
  def a1(a: Int, b: Int ): String ={
    s"""${a+b}"""
  }
  def a2(a: Int, b: Int  ): String ={
    s"""${a-b}"""
  }
  def a3(a: Int, b: Int  ): String ={
    s"""${a*b}"""
  }
  def a4(a: Int, b: Int  ): String ={
    s"""${a/b}"""
  }

  // 步骤2需要的算法
  def b1(a: Int, b: Int  ): String ={
    s"""${a > b}"""
  }

  def b2(a: Int, b: Int  ): String ={
    s"""${a < b}"""
  }

}

object Test{
  def main(args: Array[String]): Unit = {

   // b

   // System.exit(0)
    // 调用abtest
    val  s = Http("http://127.0.0.1:8080/hello").asString.body.split(",")
    println(s.mkString(","))

    // 业务逻辑 , 步骤
    fun_1(1, 2)(s(0))
    fun_1(1, 2)(s(1))


    // 业务逻辑, 步骤
    s(0) match {
      case "a1" =>fun_2(1, 2)(new Abc().a1)
      case "a2" =>fun_2(1, 2)(new Abc().a2)
      case "a3" =>fun_2(1, 2)(new Abc().a3)
      case "a4" =>fun_2(1, 2)(new Abc().a4)
    }
    s(1) match {
      case "b1" =>fun_2(1, 2)(new Abc().b1)
      case "b2" =>fun_2(1, 2)(new Abc().b2)
    }

  }

  // 使用反射调用算法
  def fun_1(a: Int, b: Int)(fun_name: String ): Unit ={

    val c = Class.forName("com.lb.scala.akka.demo.Abc")
    val haha: Method = c.getDeclaredMethod(fun_name, Integer.TYPE, Integer.TYPE)

    val re = haha.invoke(c.newInstance(), a.asInstanceOf[Object], b.asInstanceOf[Object])
   println(re)
  }

  // 使用高阶函数调用算法
  def fun_2(a: Int, b: Int)(f:(Int, Int) => String ): Unit ={
     println( f(a, b) )
    f(a, b)
  }

  // 向abtest申请策略
  def abTestConn(){
    val response: HttpResponse[String] = Http("http://127.0.0.1:8080/hello").asString
    println(response.body)
  }

  def a(){
    // 214
    val s1 = System.currentTimeMillis()
    for(i <- 0 until 10000){
      fun_1(1, 2)("a1")
    }
    println(System.currentTimeMillis() - s1)

    //12
    val s2 = System.currentTimeMillis()
    for(i <- 0 until 10000){
      fun_2(1, 2)(new Abc().a1)
    }
    println(System.currentTimeMillis() - s2)

  }

  def b(): Unit ={
    val q = Seq("a","b","","","")
    val w = Seq("a","b","w1","","")
    val e = Seq("a","b","w2","e2","")
    val r = Seq("a","b","","e3","")

    val li = List(q,w,e,r)

   //val h = li.flatMap{_}.reduce(_ zip _)

    val h =  q zip w
    println(h)
    h.foreach(println)

    val o = h.map{x =>
      x.toString().replaceAll("[\\) \\(]", "").split(",", -1).filter(_ != "") match {
      case s: Array[String] if(s.length > 0) => s(0)
      case _ => ""
    }
    }.mkString(",")
    println(o)
  }
}
