package com.lb.scala.akka.demo

import java.util.concurrent.atomic.LongAdder

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.routing.RoundRobinPool
import org.slf4j.LoggerFactory
import akka.pattern._
import scala.concurrent.duration._

class SupervisorDemo extends  Actor with  ActorLogging{

  val hehe_ref = context.actorOf(Props[Supervisor_HeHe], "SupervisorDemo_ref_hehe")
  val haha_ref = context.actorOf(Props[Supervisor_HaHa], "SupervisorDemo_ref_haha")

  context.watch(hehe_ref)
  context.watch(haha_ref)

   override def receive: Receive = {
     case  _ =>   haha_ref ! 2
     case Terminated(haha_ref) => {
       log.info("haha_ref  kill !!!!!!!!!!!!!")
     }

   }

  override def unhandled(message: Any): Unit = {
      log.info(s"info: $message")
      log.debug(s"debug: $message")

  }

  /**
    * maxNrOfRetries
    * @return
    */
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute)  {
    case _: ArithmeticException => Restart  //  Resume
    case _: RuntimeException => Restart
    case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
  }


//  override def  supervisorStrategy : SupervisorStrategy =  {
//     super.supervisorStrategy
//  }



 }

object SupervisorDemo {

  val Log = LoggerFactory.getLogger(SupervisorDemo.getClass)

  // 任务计数器
  var a : LongAdder = new LongAdder

  val sys = ActorSystem.create("SupervisorDemo_system")
  val ref = sys.actorOf(Props[SupervisorDemo].withRouter(new RoundRobinPool(3)), "SupervisorDemo_ref")



  def main(args: Array[String]): Unit = {

    for(i <- 1 to 10){
      ref ! i
      a.increment
      Thread.sleep(100)

      println(s"$i sum:  "+a.sum())
    }



    // sys.terminate()
  }

}


class Supervisor_HeHe extends Actor{
  val log = LoggerFactory.getLogger(classOf[Supervisor_HeHe].getClass)

  override def preStart(): Unit = super.preStart()
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    log.info(s"hehe preRestart, ${message.foreach(println)}")
  }
  override def postStop(): Unit = {
    super.postStop()
    log.info("hehe  stop!!")
  }
  override def postRestart(reason: Throwable): Unit = super.postRestart(reason)


  override def receive: Receive = {
    case  _ => {
      println("123123123123")
    }
  }

}

class Supervisor_HaHa extends Actor{

  val log = LoggerFactory.getLogger(classOf[Supervisor_HaHa].getClass)


  override def preStart(): Unit = super.preStart()


  //在重启时preRestart是在原来的Actor实例上调用preRestart的
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"Restarting ChildActor for ${reason.getMessage}...")
    message match {
      case Some(msg) =>
        log.info(s"Exception message: ${msg.toString}")
        self ! msg       // 把异常消息再摆放到信箱最后
      case None =>
    }
    super.preRestart(reason, message)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("HaHa  stop!!")
    // SupervisorDemo.a.decrement
  }
  override def postRestart(reason: Throwable): Unit = super.postRestart(reason)


  override def receive: Receive = {
    case  _ =>
      log.info("32131231")
      val a = new  java.util.Random
      val b=  a.nextInt(3)
      1 / 0
      println()
      log.info(s"b: $b ==========321312321312333331321312333331321312333331321312333331321312333331333331")
      SupervisorDemo.a.decrement
  }

}