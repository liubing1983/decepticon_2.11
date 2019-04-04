package com.lb.scala.base.functional.p02.exercise

object e2_2 extends App{


  println(isSorted[Int](Array(1, 2, 3, 4, 5, 3), f2))

  def f1(a: String, b: String) : Boolean ={
    a > b
  }

  def f2(a: Int, b: Int) : Boolean ={
    a < b
  }

  /**
    * 多态高阶函数
    * @param as
    * @param ordered
    * @tparam T
    * @return
    */
  def isSorted[T] (as : Array[T], ordered: (T, T) => Boolean) : Boolean ={

    def go(a: Int, b: Int,as : Array[T], ordered: (T, T) => Boolean) : Boolean = {
      if(as.length > b && as.length > 2){
        if(ordered(as(a), as(b)))  go(b, b+1, as, ordered)
        else false
      }else if(as.length < 2){
        false
      }else{
        true
      }
    }

   go(0, 1, as, ordered)
  }

}
