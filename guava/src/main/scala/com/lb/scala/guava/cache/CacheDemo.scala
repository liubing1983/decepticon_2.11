package com.lb.scala.guava.cache

import java.util.concurrent.{Callable, TimeUnit}

import com.google.common.cache.{Cache, CacheBuilder, CacheLoader}

/**
  * 测试guava cache
  */
class CacheDemo {



}

/**
  *
  */
object  CacheDemo{

  def main(args: Array[String]): Unit = {
    val cache: Cache[String, String] = CacheBuilder.newBuilder()
      //设置cache的初始大小为10，要合理设置该值
      .initialCapacity(10)
      //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
      .concurrencyLevel(5)
      //设置cache中的数据在写入之后的存活时间为10秒
      .expireAfterWrite(10, TimeUnit.SECONDS)
      //构建cache实例
      .build[String, String]

    // 缓存数据
    cache.put("123", "123")

    val map: Map[String, String] = Map("A" -> "a")

   // cache.putAll(map)

    // 缓存数据大小
    println(cache.size())




    // 获取存在的值
    println(cache.getIfPresent("123"))

    // 模拟获取不存在的值, 返回null
    println(cache.getIfPresent("122223"))

    // key不存在后重新加载
    val a = cache.get("321", new Callable[String](){
      @Override
      def call(): String ={
        "hahaha"
      }
    })

    println(a)
  }

}