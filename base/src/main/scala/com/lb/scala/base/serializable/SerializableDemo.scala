package com.lb.scala.base.serializable

import java.io._


@SerialVersionUID(-3582757317047668634L)
case class  Person(val name: String,val  age:Int) extends  Serializable{
}


object SerializableDemo {

  def main(args: Array[String]): Unit = {
    // seriizablePerson(new Person("lb", 18, "sdf"))
    val p = unSeriizablePerson

    println(p.name)
  }

  /**
    * 序列化对象
    * @param p
    */
  def seriizablePerson(p: Person){
    val oo = new ObjectOutputStream(new FileOutputStream(new File("/Users/liubing/demo/personSeriizable.txt")))
    oo.writeObject(p)
    println("Person对象序列化成功！")
    oo.close()
  }

  /**
    * 反序列化对象
    * @return
    */
  def unSeriizablePerson() : Person ={
    val ois = new ObjectInputStream(new FileInputStream(new File("/Users/liubing/demo/personSeriizable.txt")))
    ois.readObject().asInstanceOf[Person]
  }
}
