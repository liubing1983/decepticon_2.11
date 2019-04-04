package com.lb.scala.base.xml.dom4j


import scala.xml.XML

object ScalaXml extends  App{

  val fileName = "applicationContext.xml"

  // 从文件读取XML，输入文件名，返回XML文档
  val someXml = XML.load(ScalaXml.getClass.getClassLoader.getResource(fileName))


  val a = (someXml \"bean") .foreach{ x =>
    //println(x.mkString)
  }


  val b  = someXml \"abc"



  // someXml.foreach(println)

}
