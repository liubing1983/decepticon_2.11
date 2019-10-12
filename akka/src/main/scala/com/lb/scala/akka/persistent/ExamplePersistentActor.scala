package com.lb.scala.akka.persistent

import java.util
import java.util.concurrent.atomic.LongAdder

import akka.actor.{ActorLogging, ActorSystem, DeadLetter, Props}
import akka.persistence._
import akka.persistence.serialization.Snapshot
import akka.serialization.Serialization
import com.lb.scala.akka.persistent.ExamplePersistentActor.ref
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._


case class Cmd(data: String) // 命令
case class Evt(data: String) // 事件

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)

  def size: Int = events.length

  override def toString: String = events.reverse.toString
}


/**
  * @ClassName ExamplePersistentActor
  * @Description PersistentActor：任何一个需要持久化的Actor都必须继承它，并必须定义或者实现其中的三个关键属性
  *              相比普通的Actor，除receiveCommand相似以外，还必须实现另外两个属性。
  *              def persistenceId = "example"     // 作为持久化Actor的唯一表示，用于持久化或者查询时使用
  *              def receiveCommand: Receive = ??? // Actor正常运行时处理处理消息逻辑，可在这部分内容里持久化自己想要的消息
  *              def receiveRecover: Receive = ??? // Actor重启恢复是执行的逻辑
  *
  *              另外在持久化Actor中还有另外两个关键的的概念就是
  *              Journal:  用于持久化事件，
  *              Snapshot: 后者用于保存Actor的快照，两者在Actor恢复状态的时候都起到了至关重要的作用。
  * @Author liu bing
  * @Date 2019/8/1 16:15
  * @Version 1.0
  **/
class ExamplePersistentActor extends PersistentActor with ActorLogging {

  // 作为持久化Actor的唯一表示，用于持久化或者查询时使用
  override def persistenceId: String = "lb-test-PersistentActor-id"

  var state = ExampleState()

  def updateState(event: Evt): Unit = state = state.updated(event)

  def numEvents = state.size


  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("preRestart")
    // deleteMessages(1000000)
    super.preRestart(reason, message)
  }

  // Actor重启恢复是执行的逻辑
  override def receiveRecover: Receive = {
    case evt: Evt => {
      println(s"receiveRecover: " + evt.data)
      updateState(evt)
      ExamplePersistentActor.actorNum.increment()
    }
    case SnapshotOffer(_, snapshot: ExampleState) => {

      println(s"====== ${snapshot.events}")
      snapshot.events.foreach(println)

      println("-----------")
     //  state = snapshot
    }
  }

  val snapShotInterval = 10

  // Actor正常运行时处理处理消息逻辑，可在这部分内容里持久化自己想要的消息
  override def receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}")) { event =>

        1 / scala.util.Random.nextInt(50)


        println(event)
        updateState(event)
        context.system.eventStream.publish(event)

        // 业务逻辑
        ExamplePersistentActor.actorNum.increment()

        if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) {
          println(s"保存快照")
          saveSnapshot(state)
        }

      }
    case "print" => println(state)

    case deadLetter: DeadLetter => println(s"FROM CUSTOM LISTENER $deadLetter")

    case "del" => println("----------------------------------------")
      deleteMessages(1000000)

    case SaveSnapshotSuccess =>  println(s"保存快照成功")
    case SaveSnapshotFailure =>  println(s"保存快照失败")
  }


}

object ExamplePersistentActor {

  val sys = ActorSystem.create("Example-PersistentActor-system", ConfigFactory.load("examplePersistentActor.conf"))
  val ref = sys.actorOf(Props[ExamplePersistentActor], "Example-PersistentActor-ref")

  // 任务计数器, 使用原子变量
  var actorNum : LongAdder = new LongAdder

  def main(args: Array[String]): Unit = {

    ref ! "del"
    ref ! "print"
    for (i <- 0 to 100) {
      ref ! Cmd(i+"==a")
    }
    ref ! "print"

    println(actorNum.sum())


    scala.io.StdIn.readLine()


    println(actorNum.sum())
    sys.terminate()

  }

}