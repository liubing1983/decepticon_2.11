package com.lb.scala.akka.demo

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props, SupervisorStrategy, Terminated}

/**
  * @ClassName CountWatchDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2021/1/4 15:23
  * @Version 1.0
  **/
class CountWatchDemo extends Actor with ActorLogging {

  val countRef = context.actorOf(Props[CountActor], "CountWatchDemo-watch-actorOf")

  context.watch(countRef)

  override def receive: Receive = {

    case Terminated(countRef) => {
      println("子actor \"hehe\",  kill !!!!");
     // context.actorOf(Props[CountActor], "CountWatchDemo-watch-actorOf")
    }

    case i: Int => countRef ! i
  }

  override def supervisorStrategy: SupervisorStrategy = super.supervisorStrategy
}


object CountWatchDemo extends App {

  val sys = ActorSystem.create("CountWatchDemo-actor-system")
  val ref = sys.actorOf(Props[CountWatchDemo], "CountWatchDemo-actorOf")

  for (i <- 1 to 10) {
    ref ! i
  }

  val he =  sys.actorSelection("/user/CountWatchDemo-actorOf/CountWatchDemo-watch-actorOf")
  // 打印路径
  println(s"psth: "+he.anchorPath.address)
  println(s"psth: "+he.pathString)
  // 发送关闭请求 - 处理完邮箱后关闭
  he ! PoisonPill


  Thread.sleep(5000)
  sys.terminate()

}

class CountActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case i: Int =>
      val  a = (math.random * 5 ).toInt
      println(a+"------------------")
      1 / a
      log.info(i + "")
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
