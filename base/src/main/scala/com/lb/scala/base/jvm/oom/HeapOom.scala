package com.lb.scala.base.jvm.oom

/**
  * @ClassName HeapOom
  * @Description @TODO 测试堆溢出
  *             -Xms20m  -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
  * @Author liubing
  * @Date 2019/12/20 14:45
  * @Version 1.0
  **/
object HeapOom  extends  App{

  var a = "a"

  while(true){
     a = a + a
  }

  /**
    * java.lang.OutOfMemoryError: Java heap space
      Dumping heap to java_pid39473.hprof ...
      Heap dump file created [2246685 bytes in 0.019 secs]
      Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
    */

}
