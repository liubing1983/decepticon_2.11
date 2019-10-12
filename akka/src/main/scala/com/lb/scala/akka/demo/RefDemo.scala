package com.lb.scala.akka.demo

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.RoundRobinPool

/**
  * 最简单的akka  demo
  */
class ActorDemo extends  Actor with  ActorLogging {


  override def preStart(): Unit = super.preStart()
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = super.preRestart(reason, message)
  override def postStop(): Unit = super.postStop()
  override def postRestart(reason: Throwable): Unit = super.postRestart(reason)

  // 接收信息
  override def receive: Receive = {
    case i: Int => log.info(i+"")
    case s: String => log.info(s)
  }

  // context.actorOf(Props[ActorDemo])


  // 接收未匹配的数据
  override def unhandled(message: Any) ={
      log.info(message+"--------------")
  }
}

object ActorDemo {

  // 创建akka系统
  val sys = ActorSystem.create("RefDemo-actoe-system")
  // 绑定Actor
   //val ref =  sys.actorOf(Props[ActorDemo], "RefDemo-actor-ref")
   val ref = sys.actorOf(Props[ActorDemo].withRouter(new RoundRobinPool(10)), "RefDemo-actor-ref")

  def main(args: Array[String]): Unit = {
    // 发送消息
    ref ! 1
    ref ! "sss"
    ref ! 0.1
    ref ! true

    Thread.sleep(500)

    // 停止akka 系统
    sys.terminate()
  }
}



