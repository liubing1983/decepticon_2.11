package com.lb.scala.devastator.enums

/**
  * @ClassName Test
  * @Description @TODO
  * @Author liubing
  * @Date 2020/12/25 09:53
  * @Version 1.0
  **/
object Test  extends  App{

    val a = Seq(".Trash", ".sparkStaging", ".staging", "history", "user", ".tmp", ".temp", ".aux")

    val b = a.exists{x => x.equals(".temp")}

  println(b)

}
