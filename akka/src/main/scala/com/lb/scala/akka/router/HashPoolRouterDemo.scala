package com.lb.scala.akka.router

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashable

sealed class Counting(cur: String) extends ConsistentHashable {
  override def consistentHashKey: Any = cur
}
case class OneHand(cur: String, a: Int, b: Int) extends Counting(cur)




/**
  * @ClassName HashPoolRouterDemo
  * @Description @TODO
  * @Author liubing
  * @Date 2019/8/12 18:53
  * @Version 1.0
  **/
class HashPoolRouterDemo extends Actor  with ActorLogging{
  override def receive: Receive = {
    case OneHand(cur, a: Int, b:Int) =>  log.info(s" $a $cur $b ")
  }
}

// class HashPoolRouterDemo{}


object HashPoolRouterDemo extends App{

  val currencies = List("+","-","*q","/")
  val sys = ActorSystem.create("hashPoolRouterDemo-system")
  val ref = sys.actorOf(ConsistentHashingPool(nrOfInstances = 2).props(Props[HashPoolRouterDemo]), "hashPoolRouterDemo-actor")


  ref ! OneHand("+", 1,2)
  ref ! OneHand("+", 1,2)
  ref ! OneHand("+", 1,2)
  ref ! OneHand("+", 1,2)
  ref ! OneHand("+", 1,2)
  ref ! OneHand("-", 12,2)
  ref ! OneHand("-", 12,2)
  ref ! OneHand("-", 12,2)
  ref ! OneHand("-", 12,2)
  ref ! OneHand("-", 12,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("*", 6,2)
  ref ! OneHand("/", 1,2)


}
