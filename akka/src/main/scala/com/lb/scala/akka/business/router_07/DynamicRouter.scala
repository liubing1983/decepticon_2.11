package com.lb.scala.akka.business.router_07

import akka.actor._
import com.lb.scala.akka.business.CompletableApp

import scala.reflect.runtime.currentMirror

/**
  * @ClassName DynamicRouter
  * @Description @TODO
  * @Author liubing
  * @Date 2019/12/25 14:59
  * @Version 1.0
  **/
case class InterestedIn(msg: String)
case class NoLongerInterestedIn(msg: String)

case class TypeAMessage(desc: String)
case class TypeBMessage(desc: String)
case class TypeCMessage(desc: String)
case class TypeDMessage(desc: String)

object DynamicRouter extends CompletableApp(5) {
  // 通知计数器减一
  val dunnoInterestedRef = system.actorOf(Props[DunnoInterested], "dunnoInterested")

  // 注册actor
  val typedMessageInterestRouter =
    system.actorOf(Props(new TypedMessageInterestRouter(dunnoInterestedRef, 4, 1)), "typedMessageInterestRouter")

  system.actorOf(Props(classOf[TypeAInterested], typedMessageInterestRouter), "typeAInterest")
//  system.actorOf(Props(classOf[TypeBInterested], typedMessageInterestRouter), "typeBInterest")
//  system.actorOf(Props(classOf[TypeCInterested], typedMessageInterestRouter), "typeCInterest")
//  system.actorOf(Props(classOf[TypeCAlsoInterested], typedMessageInterestRouter), "typeCAlsoInterested")

  awaitCanStartNow

  typedMessageInterestRouter ! TypeAMessage("Message of TypeA.")
//  typedMessageInterestRouter ! TypeBMessage("Message of TypeB.")
//  typedMessageInterestRouter ! TypeCMessage("Message of TypeC.")
//
//  awaitCanCompleteNow
//
//  typedMessageInterestRouter ! TypeCMessage("Another message of TypeC.")
//  typedMessageInterestRouter ! TypeDMessage("Message of TypeD.")
//
//  awaitCompletion
//  println("DynamicRouter: is completed.")

}



class TypedMessageInterestRouter(dunnoInterested: ActorRef, canStartAfterRegistered: Int, canCompleteAfterUnregistered: Int) extends Actor {

  val interestRegistryMap = scala.collection.mutable.Map[String, ActorRef]()
  val secondaryInterestRegistryMap = scala.collection.mutable.Map[String, ActorRef]()

  def receive = {
    case interestedIn: InterestedIn => println(s"${interestedIn.msg}---hahah");registerInterest(interestedIn)
    case noLongerInterestedIn: NoLongerInterestedIn => println(s"${noLongerInterestedIn.msg}---hehehe"); unregisterInterest(noLongerInterestedIn)
    case message: Any => sendFor(message)
  }

  def registerInterest(interestedIn: InterestedIn) = {
    val messageType = typeOfMessage(interestedIn.msg)
    if (!interestRegistryMap.contains(messageType)) {
      interestRegistryMap(messageType) = sender
    } else {
      secondaryInterestRegistryMap(messageType) = sender
    }

    // 判断是否全部注册完成
    if (interestRegistryMap.size + secondaryInterestRegistryMap.size >= canStartAfterRegistered) {
      DynamicRouter.canStartNow()
    }
  }

  def sendFor(message: Any) = {
    val messageType = typeOfMessage(currentMirror.reflect(message).symbol.toString)

    if (interestRegistryMap.contains(messageType)) {
      interestRegistryMap(messageType) forward message
    } else {
      dunnoInterested ! message
    }
  }

  def typeOfMessage(rawMessageType: String): String = {
    rawMessageType.replace('$', ' ').replace('.', ' ').split(' ').last.trim
  }

  var unregisterCount: Int = 0

  def unregisterInterest(noLongerInterestedIn: NoLongerInterestedIn) = {
    val messageType = typeOfMessage(noLongerInterestedIn.msg)

    if (interestRegistryMap.contains(messageType)) {
      val wasInterested = interestRegistryMap(messageType)

      if (wasInterested.compareTo(sender) == 0) {
        if (secondaryInterestRegistryMap.contains(messageType)) {
          val nowInterested = secondaryInterestRegistryMap.remove(messageType)

          interestRegistryMap(messageType) = nowInterested.get
        } else {
          interestRegistryMap.remove(messageType)
        }

        unregisterCount = unregisterCount + 1
        if (unregisterCount >= this.canCompleteAfterUnregistered) {
          DynamicRouter.canCompleteNow()
        }
      }
    }
  }
}

class DunnoInterested extends Actor {

  println("lllllllbbbbbbbb")

  def receive = {
    case message: Any =>
      println(s"没有匹配对象: DunnoInterest: received undeliverable message: $message")
      // 计数器减一
      DynamicRouter.completedStep()
  }
}

class TypeAInterested(interestRouter: ActorRef) extends Actor {
  println("------------")
  // 注册actor
  interestRouter ! InterestedIn(TypeAMessage.getClass.getName)

  def receive = {
    case message: TypeAMessage =>
      println(s"TypeAInterested: received: $message")
      DynamicRouter.completedStep()
    case message: Any =>
      println(s"TypeAInterested: received unexpected message: $message")
  }
}

class TypeBInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeBMessage.getClass.getName)

  def receive = {
    case message: TypeBMessage =>
      println(s"TypeBInterested: received: $message")
      DynamicRouter.completedStep()
    case message: Any =>
      println(s"TypeBInterested: received unexpected message: $message")
  }
}

class TypeCInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeCMessage.getClass.getName)

  def receive = {
    case message: TypeCMessage =>
      println(s"TypeCInterested: received: $message")

      interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)

      DynamicRouter.completedStep()

    case message: Any =>
      println(s"TypeCInterested: received unexpected message: $message")
  }
}

class TypeCAlsoInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeCMessage.getClass.getName)

  def receive = {
    case message: TypeCMessage =>
      println(s"TypeCAlsoInterested: received: $message")

      interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)

      DynamicRouter.completedStep()
    case message: Any =>
      println(s"TypeCAlsoInterested: received unexpected message: $message")
  }
}

