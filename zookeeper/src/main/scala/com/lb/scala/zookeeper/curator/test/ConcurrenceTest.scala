package com.lb.scala.zookeeper.curator.test

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.lb.scala.zookeeper.curator.ZkConn
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}


class ConcurrenceTest extends Actor {

  var client: CuratorFramework = null

  override def preStart(): Unit = {
    client = new ZkConn().getZKConnection()
    super.preStart()
  }


  override def receive: Receive = {
    case "start" =>
    case "stop" => ConcurrenceTest.stopActorSys()
  }


  def  testZk(): Unit ={ client.getNamespace }

}

object ConcurrenceTest extends App {

  val system = ActorSystem.create("zk concurrence test system")
  val ref = system.actorOf(Props[ConcurrenceTest].withRouter(new RoundRobinPool(10)), "")

  for (i <- 0 to 100000) {
    ref ! "start"

  }


  /**
    * 关系系统连接
    */
  def stopActorSys(): Unit = {
    new ConcurrenceTest().client.close()
    system.terminate()
  }

}
