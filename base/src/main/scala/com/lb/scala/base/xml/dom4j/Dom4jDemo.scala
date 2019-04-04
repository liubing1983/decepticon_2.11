package com.lb.scala.base.xml.dom4j

import java.io.File

import org.dom4j.{Attribute, Document, Element}
import org.dom4j.io.SAXReader
import scala.collection.JavaConversions._


object Dom4jDemo extends  App{

  // 从文件读取XML，输入文件名，返回XML文档
  val document = (fileName: String)  =>  new SAXReader().read(Dom4jDemo.getClass.getClassLoader.getResourceAsStream(fileName))

  // 取得Root节点
  val getRootElement = (doc: Document) =>  doc.getRootElement

  val root = getRootElement(document("applicationContext.xml"))

  root.elements().foreach{ println }
  //root.elementIterator()


  //println(root.element("bean").attribute(1).getText)


  val it = root.attributeIterator
  while ( {
    it.hasNext
  }) {
    val attribute = it.next
    // do something
  }


  val a  = root.elements.iterator()
  // 枚举所有子节点
  while(a.hasNext){
    val element: Element = a.next().asInstanceOf[Element]

    val abc = element.attribute("key")

     // println(abc.getText)

   // println(element.attribute(0).getStringValue)


    // 打印节点文本
    // println( element.getText)
    // println( element.getStringValue)
  }


  println( "-----------------------")

  // 枚举名称为foo的节点
  val b  = root.elementIterator("bean")
  while(b.hasNext){
    val element : Element = b.next().asInstanceOf[Element]

    println(  element.getText)

  }
  println("-----------------------")

  // 枚举属性
  val c   = root.attributeIterator()
  while(c.hasNext){
    val attribute : Attribute = c.next().asInstanceOf[Attribute]
    println(attribute.getText)
    println(attribute.getQName.getName)
  }

  val d = root.attribute("bean")
  println(d.getValue)

}
