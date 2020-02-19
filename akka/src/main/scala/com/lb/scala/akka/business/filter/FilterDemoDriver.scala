package com.lb.scala.akka.business.filter

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

/**
  * @ClassName FilterDemoDriver
  * @Description @TODO
  * @Author liubing
  * @Date 2019/11/28 15:21
  * @Version 1.0
  **/
object FilterDemoDriver  extends  App {

  val system = ActorSystem.create("filterDemoDriver")

  // 步骤2, 判读字符串长度
  val ref2 = system.actorOf(Props[StringLengthFilterDemo], "stringLangFilterDemo")

  // 步骤1 , 修改字符串为大写
  val ref1 = system.actorOf(Props(classOf[UpperCaseFilterDemo], ref2), "upperCaseFilterDemo")

  ref1 ! "liubing"

}


class UpperCaseFilterDemo(nextFilter: ActorRef) extends Actor with ActorLogging{

  override def receive: Receive = {
    case s: String => nextFilter ! s.toUpperCase()
  }

}

class StringLengthFilterDemo extends  Actor with ActorLogging{

  override def receive: Receive = {
    case s: String => println(s"""${s}, length: ${s.length}""")
  }

}
