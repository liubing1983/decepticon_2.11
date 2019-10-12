package com.lb.scala.akka.demo

import akka.actor.{Actor, ActorLogging, ActorSystem, Kill, PoisonPill, Props, SupervisorStrategy, Terminated}

/**
  * akka actor的监控
  */
class ActorWatchDemo extends Actor with ActorLogging{

  // 创建子actor
  val he = context.actorOf(Props[Hehe], "hehe")
  // 监控子actor
  context.watch(he)

  override def receive: Receive = {
    // 系统自带case,  子节点死亡后回调
    case Terminated(he) => {
      println("子actor \"hehe\",  kill !!!!");
      context.actorOf(Props[Hehe], "hehe")
    }
    case _ => println("ActorWatchDemo ....... "); he ! "子 actor"
  }

  override def supervisorStrategy: SupervisorStrategy = super.supervisorStrategy

}

/**
  * 程序入口
  */
object  ActorWatchDemo{

  val sys = ActorSystem("ActorWatchDemo_system")
  val ref = sys.actorOf(Props[ActorWatchDemo], "ActorWatchDemo_ref")

  def main(args: Array[String]): Unit = {

    ref ! "a"

    // 根据路径得到actor
    // val he =  sys.actorSelection("akka://ActorWatchDemo_system/user/ActorWatchDemo_ref/hehe")
    val he =  sys.actorSelection("/user/ActorWatchDemo_ref/hehe")
    // 打印路径
    println(s"psth: "+he.anchorPath.address)
    println(s"psth: "+he.pathString)
    // 发送关闭请求 - 处理完邮箱后关闭
    he ! PoisonPill

    ref ! "a"

    // 发送关闭请求 - 马上关闭
    he ! Kill

    Thread.sleep(5000)

    ref ! "a"

    sys.terminate()
  }

}


class Hehe extends Actor{
  override def receive: Receive = {
    case s: String => println(s"""Hehe: $s""")
  }

  override def preStart(): Unit = {
    println("Hehe Actor preStart:  start")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("Hehe Actor postStop: stop")
    super.postStop()
  }
}