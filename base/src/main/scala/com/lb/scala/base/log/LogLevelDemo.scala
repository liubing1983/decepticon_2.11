package com.lb.scala.base.log

import org.slf4j.LoggerFactory

object LogLevelDemo extends App{

 val log = LoggerFactory.getLogger(LogLevelDemo.getClass)


  log.debug("========== LogLevelDemo debug =============")

  log.info("========== LogLevelDemo info =============")

  log.warn("========== LogLevelDemo warn =============")

  log.error("========== LogLevelDemo error =============")


}
