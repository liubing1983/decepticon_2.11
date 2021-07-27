package com.lb.decepticon_2

import org.slf4j.LoggerFactory


/**
  * @ClassName LbApp
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/30 10:58
  * @Version 1.0
  **/
class LbApp(val clazz: Class[_]) extends App{

  val Log = LoggerFactory.getLogger(clazz)

}
