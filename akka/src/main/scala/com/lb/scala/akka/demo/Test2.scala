package com.lb.scala.akka.demo

import scalaj.http.Http

object Test2 {

  def main(args: Array[String]): Unit = {

    // 调用ABtest系统
    val  s = Http("http://127.0.0.1:8080/demo?system=demo").asString.body.split(",")

    // 微服务1 跳转
    println(Http(s"""${s(0)}?json=s.toJson&conl=123""").asString.body)

    // 微服务2 跳转
    println(Http(s"""${s(1)}?json=s.toJson""").asString.body)
  }

}
