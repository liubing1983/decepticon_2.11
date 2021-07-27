package com.lb.scala.soundwave

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory

/**
  * @ClassName ActorApp
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/21 10:10
  * @Version 1.0
  **/
class ActorApp(clazz : String)  extends  App{

  val log = LoggerFactory.getLogger(clazz)

  val system = ActorSystem(s"${clazz}_ActorSystem")


}
