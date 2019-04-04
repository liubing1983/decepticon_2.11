package com.lb.scala.base.functional.p03.example

import com.lb.scala.base.functional.p03.example.List.sum

/**
  * 泛型数据结构
  * +A 协变
  *
  * @tparam A
  */
sealed trait List[+A]

// 用于表示空list的list数据的构造器
case object Nil extends List[Nothing]

case class Cons[+A](head: A, tail: List[A]) extends List[A]

/**
  * List的伴生对象. 包含创建List和对List操作的一些函数
  */
object List {


  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x, xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = {
    // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }

  /**
    * 习题 3-2  删除list的第一个元素
    *
    * @param list
    * @return
    */
  def tail(list: List[Int]): List[Int] = {
    list match {
      case Nil => Nil
      case Cons(x, t) => t
    }
  }

  /**
    * 习题 3-3  修改list的第一个元素
    *
    * @param list
    * @return
    */
  def setHead(list: List[Int], a: Int): List[Int] = {
    list match {
      case Nil => Nil
      case Cons(x, t) => Cons(a, t)
    }
  }

  /**
    * 习题 3-4   删除删n个元素
    *
    * @param list
    * @param i
    * @return
    */
  def drop(list: List[Int], i: Int): List[Int] = {

    def go(list: List[Int], i: Int, n: Int): List[Int] = {
      if (i == n) list
      else go(tail(list), i, n + 1)
    }

    go(list, i, 0)
  }

  /**
    * 习题 3-5  删除列表中前缀全部符合判定的元素
    *
    * @param l
    * @param f
    * @tparam A
    * @return
    */
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = {
    l match {
      case Cons(h, t) if f(h) => dropWhile(t, f)
      case _ => l
    }
  }

  /**
    * 习题 3-6 删除最后一个元素
    *
    * @param l
    * @tparam A
    * @return
    */
  def init[A](l: List[A]): List[A] = {
    println(l)
    l match {
      case Cons(_, Nil) => Nil
      case Cons(h, t) => println(h); Cons(h, init(t))
    }
  }

  /**
    * 示例 3.2 有折叠的简单应用
    *
    * @param as
    * @param z
    * @param f
    * @tparam A
    * @tparam B
    * @return
    */
  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B = {
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }
  }

  def sum2(ns: List[Int]) = foldRight(ns, 0)(_ + _)

  def product2(ns: List[Double]) = foldRight(ns, 1.0)(_ * _)

  /**
    * 习题 3-9 计算list的长度
    *
    * @param as
    * @return
    */
  def length[A](as: List[A]) = foldRight(as, 0)((x, y) => y + 1)


  /**
    * 3-10
    * @param as
    * @param z
    * @param f
    * @tparam A
    * @tparam B
    * @return
    */
  def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B): B = {

    as match {
      case Nil => z
      case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
    }
  }

  //def sum2FoldLeft[A, Int](list: List[A]): Int={
    //foldLeft[A, Int](list, 0)(_+_)
      null
 // }

}

object TestList extends App {
  val l = List[Int](1, 2, 3, 4, 5)
  val l1 = List[Double](0.0, 0.2)
  val l2 = Nil
  val l3 = Cons(1, Cons(2, Nil))
  /**
    * 习题3-1
    */
  val x = l match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y // 符合
    case Cons(h, t) => h + sum(t) // 符合, 但先执行上一个case
    case _ =>
  }

  // println(x)
  // val y = List.tail(l)
  // println(y)

  // println(List.drop(l, 10))
  // println(List.init(l))

  println(List.product2(l1))
  println(List.sum2(l2))
  println(List.sum2(l3))
  println(s"3-9 list长度: ${List.length(l2)}")
}
