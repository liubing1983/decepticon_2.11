package com.lb.scala.base.aop.frame

import java.io.File
import java.util
import javax.lang.model.element.Element

import org.dom4j.Document
import org.dom4j.io.SAXReader

object AopFrameDemo {

  def  abc: Unit = {




  }


  def newInstance(className: String) : Any ={
    var cla : Class[_] = null
    var  obj : Any = null
    try {
      cla = Class.forName(className)
      obj = cla.newInstance()
    } catch {
      case e:ClassNotFoundException => e.printStackTrace()
      case e:InstantiationException => e.printStackTrace()
      case e:IllegalAccessException => e.printStackTrace()
    }
  }




}
