package com.lb.scala.base.log

import org.slf4j.LoggerFactory

object LogDemo {

  def main(args: Array[String]): Unit = {

    val  log = LoggerFactory.getLogger("LogDemo")

    log.debug("debug")
    log.info("info")
    log.warn("warn")
    log.error("error")
  }

}
