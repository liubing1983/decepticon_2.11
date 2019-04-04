package com.lb.scala.base.annotation

import com.sun.istack.internal.{NotNull, Nullable}

import scala.beans.BeanProperty

@BeanProperty
case class Abc(@NotNull a: String, b:Int, c:Boolean, d:Number, e: String)

class MyAnnotation {

}




object  MyAnnotation{
  def main(args: Array[String]): Unit = {
    Abc.->(println)

    val a = Abc


   val clazz = classOf[com.lb.scala.base.annotation.Abc]

    val field = clazz.getDeclaredFields

    println(field.size)

    println(field.foreach(println))

    val s = s""",1,true,1.1,321"""

      clazz.getMethods.foreach(println)

  }
}