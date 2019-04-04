package com.lb.scala.akka.demo

import java.util.concurrent.atomic.LongAdder

import akka.actor._
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import akka.pattern.ask
import akka.routing.RoundRobinPool

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

// akka 监控的消息类型
trait Message
case class stringMessage(s: String) extends Message
case class intMessage(a: Int, b: Int)
case class intMessage2(a: Int, b: Int)
case class msgMessage(a: Int, b: Int, ref: ActorRef)
case class returnMessage(s: String)



class SenderTestActor  extends Actor with ActorLogging {
  override def receive: Receive = {
    case msgMessage(a, b, c) => {
      log.info("123456789")
      c ! returnMessage((a + b).toString)
      // 任务完成后原子变量-1
      HelloWorld.a.decrement()
    }
  }
}

class HelloWorld extends Actor with ActorLogging {

  // 得到子actor
  val senderTestActor = context.actorOf(Props[SenderTestActor], "senderTestActor")

  context.watch(senderTestActor)

  override def receive: Receive = {
    case stringMessage(s) => log.info(s"Hello, $s")

    case intMessage(a, b) =>  Thread.sleep(2000); sender ! a + b

    // 将得到的任务的发送给子actor
    case intMessage2(a, b) => {
      senderTestActor ! msgMessage(a, b , self)
      // 发送任务后原子变量+1
      HelloWorld.a.increment()

    }
    // 接受子actor返回的结果
    case returnMessage(s : String ) => {
      log.info("22222-"+s)

    }
    case "stop" => HelloWorld.shutdowd()
    case _ => log.info(s"error")
  }

}


object HelloWorld extends App {

  // 任务计数器
  var a : LongAdder = new LongAdder

  val system = ActorSystem("qwe", ConfigFactory.load())
  val ref = system.actorOf(Props[HelloWorld].withRouter(new RoundRobinPool(2)), name = "123")

  // 1. 发送任务, 没有返回值
  ref ! stringMessage("Akka")

  /**
    * 2. 发送任务, 有返回值 - 方法1
    * 需要设置延时时间, 不适合任务调度
    * 官方也不建议使用
    */
  implicit val timeout = Timeout(10 seconds)
  val s = ref ? intMessage(10, 12)
  println(Await.result(s, timeout.duration).asInstanceOf[Int] + "-222")



  /**
    * 3. 发送任务, 有返回值 - 方法2
    * 需要子actor, 结构稍微复杂
    */
  for(i <- 0 to 100){
    ref ! intMessage2(i, 120)
    a.add(1)

  }

 // ref ! "stop"

  while (a.sum() > 0){
    println(s"""任务剩余${a.sum()}""")
    Thread.sleep(1000)
  }

  def shutdowd(){

  }
}
