package com.lb.flink19

/**
  * @ClassName Test
  * @Description @TODO
  * @Author liubing
  * @Date 2020/3/9 14:43
  * @Version 1.0
  **/
object Test extends  App{


  val s= ((0x4e00 + (Math.random() * (0x9fa5 - 0x4e00 + 1)).toInt).toChar).toString

     println(s)

}
