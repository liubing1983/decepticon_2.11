package com.lb.scala.akka

import akka.actor.{Actor, ActorLogging}

case class PingMsg(Pang : Actor)


class PingActor extends Actor{
  override def receive: Receive = {
    case "" => ""
  }
}

class PangActor extends Actor{
  override def receive: Receive = ???
}

class PingPang extends Actor with ActorLogging{

  override def receive: Receive = {
    case "" =>

  }
}

object  PingPang{

}
